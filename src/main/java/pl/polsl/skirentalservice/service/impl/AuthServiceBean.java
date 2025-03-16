package pl.polsl.skirentalservice.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailServiceBean;
import pl.polsl.skirentalservice.core.mail.MailTemplate;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.ssh.*;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.OtaTokenDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.OtaTokenDaoHib;
import pl.polsl.skirentalservice.dto.change_password.ChangeForgottenPasswordReqDto;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;
import pl.polsl.skirentalservice.dto.first_access.FirstAccessReqDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.entity.OtaTokenEntity;
import pl.polsl.skirentalservice.exception.CredentialException;
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.util.Utils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class AuthServiceBean implements AuthService {
    private final PersistenceBean persistenceBean;
    private final MailServiceBean mailServiceBean;
    private final SshSocketBean sshSocketBean;
    private final ServerConfigBean serverConfigBean;

    @Inject
    public AuthServiceBean(
        PersistenceBean persistenceBean,
        MailServiceBean mailServiceBean,
        SshSocketBean sshSocketBean,
        ServerConfigBean serverConfigBean
    ) {
        this.persistenceBean = persistenceBean;
        this.mailServiceBean = mailServiceBean;
        this.sshSocketBean = sshSocketBean;
        this.serverConfigBean = serverConfigBean;
    }

    @Override
    public LoggedUserDataDto loginUser(LoginFormReqDto reqDto) {
        return persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);

            final String password = employerDao
                .findEmployerPassword(reqDto.getLoginOrEmail())
                .orElseThrow(() -> new CredentialException.InvalidCredentialsException(reqDto));

            if (!(BCrypt.verifyer().verify(reqDto.getPassword().toCharArray(), password).verified)) {
                throw new CredentialException.InvalidCredentialsException(reqDto);
            }
            final LoggedUserDataDto employer = employerDao
                .findLoggedEmployerDetails(reqDto.getLoginOrEmail())
                .orElseThrow(() -> new CredentialException.InvalidCredentialsException(reqDto));

            session.getTransaction().commit();
            log.info("Successful logged on {} account. Account data: {}", employer.getRoleEng(), employer);
            return employer;
        });
    }

    @Override
    public void checkUserAndSendToken(RequestToChangePasswordReqDto reqDto, WebServletRequest req) {
        persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);

            final var employer = employerDao
                .findEmployerDetails(reqDto.getLoginOrEmail())
                .orElse(null);
            if (employer == null) {
                log.error("User with login or email address: {} not found", reqDto.getLoginOrEmail());
                return;
            }
            final String token = RandomStringUtils.randomAlphanumeric(10);
            final EmployerEntity employerEntity = session.getReference(EmployerEntity.class, employer.id());
            final OtaTokenEntity otaToken = new OtaTokenEntity(token, employerEntity);
            session.persist(otaToken);

            final Map<String, Object> templateVars = new HashMap<>();
            templateVars.put("token", token);

            final MailRequestPayload payload = MailRequestPayload.builder()
                .messageResponder(employer.fullName())
                .subject("Zmiana hasła dla użytkownika " + employer.fullName())
                .template(MailTemplate.CHANGE_PASSWORD)
                .templateVars(templateVars)
                .build();
            mailServiceBean.sendMessage(employer.emailAddress(), payload, req);

            session.getTransaction().commit();
            log.info("Successfully send token for forgot password request for employer: '{}'", employer);
        });
    }

    @Override
    public boolean isFirstAccessOrIsSellerAccount(LoggedUserDataDto employer) {
        return employer.getIsFirstAccess() && employer.getRoleAlias().equals(UserRole.SELLER.getAlias());
    }

    @Override
    public void changePassword(String token, ChangeForgottenPasswordReqDto reqDto, WebServletRequest req) {
        persistenceBean.startTransaction(session -> {
            final OtaTokenDao otaTokenDao = new OtaTokenDaoHib(session);
            final EmployerDao employerDao = new EmployerDaoHib(session);

            final var details = otaTokenDao
                .findTokenDetails(token)
                .orElseThrow(() -> new CredentialException.OtaTokenNotFoundException(req, token));

            employerDao.updateEmployerPassword(Utils.generateHash(reqDto.getPassword()), details.id());
            otaTokenDao.manuallyExpiredOtaToken(details.tokenId());

            session.getTransaction().commit();
            log.info("Successful change password for employer account. Details: {}", details);
        });
    }

    @Override
    public ChangePasswordEmployerDetailsDto getChangePasswordEmployerDetails(String token, WebServletRequest req) {
        return persistenceBean.startNonTransactQuery(session -> {
            final OtaTokenDao otaTokenDao = new OtaTokenDaoHib(session);
            return otaTokenDao
                .findTokenRelatedToEmployer(token)
                .orElseThrow(() -> new CredentialException.OtaTokenNotFoundException(req, token));
        });
    }

    @Override
    public void prepareEmployerAccount(FirstAccessReqDto reqDto, LoggedUserDataDto loggedUserDataDto) {
        persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            employerDao.updateEmployerFirstAccessPassword(Utils
                .generateHash(reqDto.getPassword()), loggedUserDataDto.getId());

            if (!serverConfigBean.isSshEnabled()) {
                final Map<String, String> commandArgs = Map.of(
                    "email", loggedUserDataDto.getEmailAddress(),
                    "newPassword", reqDto.getEmailPassword()
                );
                final var createResult = sshSocketBean
                    .executeCommand(XMLSshCommands::getUpdateMailboxPassword, commandArgs, BaseCommandResponse.class);
                if (ReturnCode.isInvalid(createResult)) {
                    throw new CommandPerformException("Nieudane zaktualizowanie hasła skrzynki pocztowej.",
                        createResult.getMsg());
                }
            }
            loggedUserDataDto.setIsFirstAccess(false);
            session.getTransaction().commit();

            log.info("Successful changed default account password and mailbox password for user: {}", loggedUserDataDto);
        });
    }

    @Override
    public boolean checkIfTokenIsExist(String token) {
        return persistenceBean.startNonTransactQuery(session -> {
            final OtaTokenDao otaTokenDao = new OtaTokenDaoHib(session);
            return otaTokenDao.checkIfTokenExist(token);
        });
    }
}
