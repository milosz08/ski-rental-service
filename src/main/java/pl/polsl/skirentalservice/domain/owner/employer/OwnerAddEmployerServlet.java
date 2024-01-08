/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.modelmapper.ModelMapper;
import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ModelMapperGenerator;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.core.mail.MailRequestPayload;
import pl.polsl.skirentalservice.core.mail.MailSocketSingleton;
import pl.polsl.skirentalservice.core.mail.MailTemplate;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.UserDetailsDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dao.hibernate.UserDetailsDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.dto.employer.AddEmployerMailPayload;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.entity.LocationAddressEntity;
import pl.polsl.skirentalservice.entity.RoleEntity;
import pl.polsl.skirentalservice.entity.UserDetailsEntity;
import pl.polsl.skirentalservice.ssh.ExecCommand;
import pl.polsl.skirentalservice.ssh.ExecCommandPerformer;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.PeselAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.PhoneNumberAlreadyExistException;

@Slf4j
@WebServlet("/owner/add-employer")
public class OwnerAddEmployerServlet extends HttpServlet {
    private final ModelMapper modelMapper = ModelMapperGenerator.getModelMapper();
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();
    private final MailSocketSingleton mailSocket = MailSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = Utils.getAndDestroySessionAlert(req, SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT);
        var resDto = Utils.getFromSessionAndDestroy(req, getClass().getName(), AddEditEmployerResDto.class);
        if (resDto == null) {
            resDto = new AddEditEmployerResDto();
        }
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", PageTitle.OWNER_ADD_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/add-employer");
            return;
        }

        final ExecCommand commandPerformer = new ExecCommandPerformer();
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            String email = "";
            try {
                session.beginTransaction();

                final EmployerDao employerDao = new EmployerDaoHib(session);
                final UserDetailsDao userDetailsDao = new UserDetailsDaoHib(session);

                if (userDetailsDao.checkIfEmployerWithSamePeselExist(reqDto.getPesel(), null)) {
                    throw new PeselAlreadyExistException(reqDto.getPesel(), UserRole.SELLER);
                }
                if (userDetailsDao.checkIfEmployerWithSamePhoneNumberExist(reqDto.getPhoneNumber(), null)) {
                    throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), UserRole.SELLER);
                }
                final RoleEntity role = session.get(RoleEntity.class, 1);
                if (role == null) {
                    throw new RuntimeException("Podana rola nie istnieje w systemie.");
                }
                String login;
                boolean emailExist;
                do {
                    final String withoutAccents = StringUtils.stripAccents(reqDto.getFirstName().substring(0, 3) +
                        reqDto.getLastName().substring(0, 3));
                    login = withoutAccents.toLowerCase(Locale.ENGLISH) + RandomStringUtils.randomNumeric(3);
                    emailExist = employerDao.checkIfLoginAlreadyExist(login);
                } while (emailExist);

                email = login + mailSocket.getDomain();
                final String mailPassword = RandomStringUtils.randomAlphanumeric(10);
                final String passwordRaw = RandomStringUtils.randomAlphanumeric(10);
                final String passowordDecoded = Utils.generateHash(passwordRaw);

                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                userDetails.setBornDate(reqDto.getParsedBornDate());
                userDetails.setEmailAddress(email);
                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                locationAddress.setApartmentNo(StringUtils.trimToNull(reqDto.getApartmentNr()));
                final EmployerEntity employer = EmployerEntity.builder()
                    .login(login)
                    .password(passowordDecoded)
                    .hiredDate(reqDto.getParsedHiredDate())
                    .locationAddress(locationAddress)
                    .userDetails(userDetails)
                    .role(role)
                    .build();

                commandPerformer.createMailbox(email, mailPassword);
                final var adminDetails = (LoggedUserDataDto) httpSession
                    .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
                final var employerMailDetails = new AddEmployerMailPayload(reqDto, email, mailPassword);

                final MailRequestPayload creatorPayload = MailRequestPayload.builder()
                    .messageResponder(adminDetails.getFullName())
                    .subject("Dodanie nowego pracownika " + employerMailDetails.getFullName())
                    .template(MailTemplate.ADD_NEW_EMPLOYER_CREATOR)
                    .templateVars(Map.of("employer", employerMailDetails))
                    .build();
                final MailRequestPayload requesterPayload = MailRequestPayload.builder()
                    .messageResponder(employerMailDetails.getFullName())
                    .subject("Witamy w systemie ")
                    .template(MailTemplate.ADD_NEW_EMPLOYER_REQUESTER)
                    .templateVars(Map.of("employerLogin", login, "employerPassword", passwordRaw))
                    .build();

                mailSocket.sendMessage(adminDetails.getEmailAddress(), creatorPayload, req);
                mailSocket.sendMessage(email, requesterPayload, req);

                session.persist(employer);
                session.getTransaction().commit();
                log.info("Employer with mailbox was successfuly created. User data: {}", employer);
                alert.setMessage(
                    "Nastąpiło pomyślnie dodanie nowego pracownika. Na adres email <strong>" + email + "</strong> " +
                        "zostało wysłane hasło dostępu do konta. Hasło dostępu do skrzynki email użytkownika znajdziesz " +
                        "w przysłanej na Twój adres email wiadomości."
                );
                alert.setType(AlertType.INFO);
                httpSession.setAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                if (!StringUtils.isEmpty(email)) commandPerformer.deleteMailbox(email);
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT.getName(), alert);
            log.error("Unable to create employer. Cause: {}", ex.getMessage());
            res.sendRedirect("/owner/add-employer");
        }
    }
}
