package pl.polsl.skirentalservice.service.impl;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ModelMapperBean;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.db.PersistenceBean;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailServiceBean;
import pl.polsl.skirentalservice.core.mail.MailTemplate;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.ServletPagination;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.core.ssh.CommandPerformException;
import pl.polsl.skirentalservice.core.ssh.ReturnCode;
import pl.polsl.skirentalservice.core.ssh.SshSocketBean;
import pl.polsl.skirentalservice.core.ssh.XMLSshCommands;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.UserDetailsDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.UserDetailsDaoHib;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.entity.LocationAddressEntity;
import pl.polsl.skirentalservice.entity.RoleEntity;
import pl.polsl.skirentalservice.entity.UserDetailsEntity;
import pl.polsl.skirentalservice.exception.AlreadyExistException;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.util.Utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Stateless
@SuppressWarnings("unused")
public class OwnerEmployerServiceBean implements OwnerEmployerService {
    private final PersistenceBean persistenceBean;
    private final MailServiceBean mailServiceBean;
    private final ModelMapperBean modelMapperBean;
    private final ServerConfigBean serverConfigBean;
    private final SshSocketBean sshSocketBean;
    private final ValidatorBean validatorBean;

    @Inject
    public OwnerEmployerServiceBean(
        PersistenceBean persistenceBean,
        MailServiceBean mailServiceBean,
        ModelMapperBean modelMapperBean,
        ServerConfigBean serverConfigBean,
        SshSocketBean sshSocketBean,
        ValidatorBean validatorBean
    ) {
        this.persistenceBean = persistenceBean;
        this.mailServiceBean = mailServiceBean;
        this.modelMapperBean = modelMapperBean;
        this.serverConfigBean = serverConfigBean;
        this.sshSocketBean = sshSocketBean;
        this.validatorBean = validatorBean;
    }

    @Override
    public Slice<EmployerRecordResDto> getPageableEmployers(PageableDto pageableDto) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            final Long totalEmployers = employerDao.findAllEmployersCount(pageableDto.filterData());

            final ServletPagination pagination = new ServletPagination(pageableDto.page(),
                pageableDto.total(), totalEmployers);
            if (pagination.checkIfIsInvalid()) {
                return new Slice<>(pagination);
            }
            final List<EmployerRecordResDto> employersList = employerDao
                .findAllPageableEmployersRecords(pageableDto);
            return new Slice<>(pagination, employersList);
        });
    }

    @Override
    public AddEditEmployerResDto getEmployerOrOwnerEditDetails(Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            final AddEditEmployerReqDto addEditEmployerReqDto = employerDao
                .findEmployerEditPageDetails(employerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(employerId));
            return new AddEditEmployerResDto(validatorBean, addEditEmployerReqDto);
        });
    }

    @Override
    public EmployerDetailsResDto getEmployerFullDetails(Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            return employerDao
                .findEmployerPageDetails(employerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(employerId));
        });
    }

    @Override
    public String addEmplyerByOwner(
        AddEditEmployerReqDto reqDto, LoggedUserDataDto loggedUserDataDto, WebServletRequest req
    ) {
        final AtomicReference<String> email = new AtomicReference<>(StringUtils.EMPTY);
        return persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);

            if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), null)) {
                throw new AlreadyExistException.PeselAlreadyExistException(reqDto.getPesel(), UserRole.SELLER);
            }
            if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                throw new AlreadyExistException
                    .PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.SELLER);
            }
            final RoleEntity role = session.get(RoleEntity.class, 1);
            if (role == null) {
                throw new RuntimeException("Podana rola nie istnieje w systemie.");
            }
            final String login = generateEmployerLogin(reqDto, employerDao);
            final String mailPassword = RandomStringUtils.randomAlphanumeric(10);
            final String passwordRaw = RandomStringUtils.randomAlphanumeric(10);

            email.set(login + mailServiceBean.getDomain());
            final EmployerEntity employer = createEmployerEntity(reqDto, login, email, passwordRaw, role);
            createEmployerMailbox(email, mailPassword);

            final var employerMailDetails = new AddEmployerMailPayload(reqDto, email.get(), mailPassword);
            sendEmailMessageAfterCreatedEmployer(employerMailDetails, loggedUserDataDto, email, login, passwordRaw, req);

            session.persist(employer);
            session.getTransaction().commit();

            log.info("Employer with mailbox was successfuly created. User data: {}", employer);
            return email.get();
        }, () -> {
            final String emailValue = email.get();
            if (!emailValue.equals(StringUtils.EMPTY) && !serverConfigBean.getEnvironment().isDevOrDocker()) {
                deleteEmployerMailbox(emailValue);
            }
        });
    }

    @Override
    public void editUserAccount(Long userId, AddEditEmployerReqDto reqDto, UserRole role) {
        persistenceBean.startTransaction(session -> {
            final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);
            final String roleName = StringUtils.capitalize(role.getEng());

            final EmployerEntity updatableUser = session.get(EmployerEntity.class, userId);
            if (updatableUser == null) {
                throw new NotFoundException.UserNotFoundException(userId);
            }
            if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), updatableUser.getId())) {
                throw new AlreadyExistException.PeselAlreadyExistException(reqDto.getPesel(), role);
            }
            if (userDetailsDao
                .checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), updatableUser.getId())) {
                throw new AlreadyExistException
                    .PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), role);
            }
            modelMapperBean.map(reqDto, updatableUser.getUserDetails());
            modelMapperBean.map(reqDto, updatableUser.getLocationAddress());
            modelMapperBean.map(reqDto, updatableUser);

            session.getTransaction().commit();
            log.info("{} with id: {} was successfuly updated. Data: {}", roleName, userId, reqDto);
        });
    }

    @Override
    public void deleteEmployer(Long employerId) {
        persistenceBean.startTransaction(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);

            final EmployerEntity deletingEmployer = employerDao
                .findEmployerBasedId(employerId)
                .orElseThrow(() -> new NotFoundException.UserNotFoundException(employerId));

            final String employerEmail = deletingEmployer.getEmailAddress();
            if (employerDao.checkIfEmployerHasOpenedRents(employerId)) {
                throw new AlreadyExistException.EmployerHasOpenedRentsException();
            }
            session.remove(deletingEmployer);
            deleteEmployerMailbox(employerEmail);

            session.getTransaction().commit();
            log.info("Employer with id: {} was succesfuly removed from system.", employerId);
        });
    }

    @Override
    public boolean checkIfEmployerExist(Long employerId) {
        return persistenceBean.startNonTransactQuery(session -> {
            final EmployerDao employerDao = new EmployerDaoHib(session);
            return employerDao.checkIfEmployerExist(employerId);
        });
    }

    private String generateEmployerLogin(AddEditEmployerReqDto reqDto, EmployerDao employerDao) {
        String login;
        boolean emailExist;
        do {
            final String withoutAccents = StringUtils.stripAccents(reqDto.getFirstName().substring(0, 3) +
                reqDto.getLastName().substring(0, 3));
            login = withoutAccents.toLowerCase(Locale.ENGLISH) + RandomStringUtils.randomNumeric(3);
            emailExist = employerDao.checkIfLoginAlreadyExist(login);
        } while (emailExist);
        return login;
    }

    private EmployerEntity createEmployerEntity(
        AddEditEmployerReqDto reqDto, String login, AtomicReference<String> email, String passwordRaw,
        RoleEntity roleEntity
    ) {
        final UserDetailsEntity userDetails = modelMapperBean.map(reqDto, UserDetailsEntity.class);
        userDetails.setBornDate(reqDto.getParsedBornDate());
        userDetails.setEmailAddress(email.get());

        final LocationAddressEntity locationAddress = modelMapperBean.map(reqDto, LocationAddressEntity.class);
        locationAddress.setApartmentNo(StringUtils.trimToNull(reqDto.getApartmentNo()));

        return EmployerEntity.builder()
            .login(login)
            .password(Utils.generateHash(passwordRaw))
            .hiredDate(reqDto.getParsedHiredDate())
            .locationAddress(locationAddress)
            .userDetails(userDetails)
            .role(roleEntity)
            .build();
    }

    private void createEmployerMailbox(AtomicReference<String> mailboxEmail, String mailboxPassword) {
        if (serverConfigBean.getEnvironment().isDevOrDocker()) {
            return;
        }
        final Map<String, String> createMailboxEntries = Map.of(
            "email", mailboxEmail.get(),
            "password", mailboxPassword
        );
        final var createResult = sshSocketBean.executeCommand(XMLSshCommands::getCreateMailbox, createMailboxEntries);
        if (ReturnCode.isInvalid(createResult)) {
            throw new CommandPerformException("Nieudane utworzenie skrzynki pocztowej pracownika.",
                createResult.getMsg());
        }
        final Map<String, String> etCapacityEntries = Map.of(
            "email", mailboxEmail.get()
        );
        final var setCapacityResult = sshSocketBean
            .executeCommand(XMLSshCommands::getSetMailboxCapacity, etCapacityEntries);
        if (ReturnCode.isInvalid(createResult)) {
            throw new CommandPerformException("Nieudane ustawienie limitu powierzchni skrzynki pocztowej pracownika.",
                setCapacityResult.getMsg());
        }
    }

    private void sendEmailMessageAfterCreatedEmployer(
        AddEmployerMailPayload mailPayload, LoggedUserDataDto ownerAccount, AtomicReference<String> email,
        String login, String password, WebServletRequest req
    ) {
        final MailRequestPayload creatorPayload = MailRequestPayload.builder()
            .messageResponder(ownerAccount.getFullName())
            .subject("Dodanie nowego pracownika " + mailPayload.getFullName())
            .template(MailTemplate.ADD_NEW_EMPLOYER_CREATOR)
            .templateVars(Map.of("employer", mailPayload))
            .build();

        final MailRequestPayload requesterPayload = MailRequestPayload.builder()
            .messageResponder(mailPayload.getFullName())
            .subject("Witamy w systemie")
            .template(MailTemplate.ADD_NEW_EMPLOYER_REQUESTER)
            .templateVars(Map.of("employerLogin", login, "employerPassword", password))
            .build();

        mailServiceBean.sendMessage(ownerAccount.getEmailAddress(), creatorPayload, req);
        mailServiceBean.sendMessage(email.get(), requesterPayload, req);
    }

    private void deleteEmployerMailbox(String mail) {
        final var deleteMailboxResult = sshSocketBean
            .executeCommand(XMLSshCommands::getSetMailboxCapacity, Map.of("email", mail));
        if (ReturnCode.isInvalid(deleteMailboxResult)) {
            throw new CommandPerformException("Nieudane usunięcie skrzynki pocztowej.", deleteMailboxResult.getMsg());
        }
        log.info("Deleted mailbox for user with email: {}", mail);
    }
}
