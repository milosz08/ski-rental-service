/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: OwnerAddEmployerServlet.java
 *  Last modified: 08/02/2023, 22:12
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;
import org.modelmapper.ModelMapper;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.Map;
import java.io.IOException;

import pl.polsl.skirentalservice.ssh.*;
import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.core.ssh.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.RandomStringUtils.*;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.UserRole.SELLER;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_ADD_EMPLOYER_PAGE;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.core.ModelMapperGenerator.getModelMapper;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-employer")
public class OwnerAddEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();
    private final ModelMapper modelMapper = getModelMapper();

    @EJB private ValidatorBean validator;
    @EJB private MailSocketBean mailSocket;
    @EJB private SshSocketBean sshSocket;
    @EJB private ConfigBean config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = getAndDestroySessionAlert(req, OWNER_ADD_EMPLOYER_PAGE_ALERT);
        var resDto = getFromSessionAndDestroy(req, getClass().getName(), AddEditEmployerResDto.class);
        if (isNull(resDto)) resDto = new AddEditEmployerResDto();
        req.setAttribute("alertData", alert);
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("title", OWNER_ADD_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/owner/add-employer");
            return;
        }

        final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
        try (final Session session = sessionFactory.openSession()) {
            reqDto.validateDates(config);
            String email = "";
            try {
                session.beginTransaction();

                final String jpqlFindPesel =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d WHERE d.pesel = :pesel";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel()).getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel(), SELLER);

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d WHERE d.phoneNumber = :phoneNumber";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber()).getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber(), SELLER);

                final RoleEntity role = session.get(RoleEntity.class, 1);
                if (isNull(role)) throw new RuntimeException("Podana rola nie istnieje w systemie.");

                String login;
                Boolean emailExist;
                do {
                    final String withoutAccents = stripAccents(reqDto.getFirstName().substring(0, 3) +
                        reqDto.getLastName().substring(0, 3));
                    login = withoutAccents.toLowerCase(ENGLISH) + randomNumeric(3);

                    final String jpqlFindMathEmail =
                        "SELECT COUNT(e.id) > 0 FROM EmployerEntity e WHERE e.login = :loginSeq";
                    emailExist = session.createQuery(jpqlFindMathEmail, Boolean.class)
                        .setParameter("loginSeq", login).getSingleResult();
                } while (emailExist);

                email = login + mailSocket.getDomain();
                final String mailPassword = randomAlphanumeric(10);
                final String passwordRaw = randomAlphanumeric(10);
                final String passowordDecoded = Utils.generateHash(passwordRaw);

                final UserDetailsEntity userDetails = modelMapper.map(reqDto, UserDetailsEntity.class);
                userDetails.setBornDate(reqDto.getParsedBornDate());
                userDetails.setEmailAddress(email);
                final LocationAddressEntity locationAddress = modelMapper.map(reqDto, LocationAddressEntity.class);
                locationAddress.setApartmentNr(trimToNull(reqDto.getApartmentNr()));
                final EmployerEntity employer = EmployerEntity.builder()
                    .login(login)
                    .password(passowordDecoded)
                    .hiredDate(reqDto.getParsedHiredDate())
                    .locationAddress(locationAddress)
                    .userDetails(userDetails)
                    .role(role)
                    .build();

                commandPerformer.createMailbox(email, mailPassword);
                final var adminDetails = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
                final var employerMailDetails = new AddEmployerMailPayload(reqDto, email, mailPassword);

                final MailRequestPayload creatorPayload = MailRequestPayload.builder()
                    .messageResponder(adminDetails.getFullName())
                    .subject("SkiRent Service | Dodanie nowego pracownika " + employerMailDetails.getFullName())
                    .templateName("add-new-employer-creator.template.ftl")
                    .templateVars(Map.of("employer", employerMailDetails))
                    .build();
                final MailRequestPayload requesterPayload = MailRequestPayload.builder()
                    .messageResponder(employerMailDetails.getFullName())
                    .subject("SkiRent Service | Witamy w systemie ")
                    .templateName("add-new-employer-requester.template.ftl")
                    .templateVars(Map.of("employerLogin", login, "employerPassword", passwordRaw))
                    .build();

                mailSocket.sendMessage(adminDetails.getEmailAddress(), creatorPayload, req);
                mailSocket.sendMessage(email, requesterPayload, req);

                session.persist(employer);
                session.getTransaction().commit();
                LOGGER.info("Employer with mailbox was successfuly created. User data: {}", employer);
                alert.setMessage(
                    "Nastąpiło pomyślnie dodanie nowego pracownika. Na adres email <strong>" + email + "</strong> " +
                    "zostało wysłane hasło dostępu do konta. Hasło dostępu do skrzynki email użytkownika znajdziesz " +
                    "w przysłanej na Twój adres email wiadomości."
                );
                alert.setType(INFO);
                httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(getClass().getName());
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                if (!isEmpty(email)) commandPerformer.deleteMailbox(email);
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(OWNER_ADD_EMPLOYER_PAGE_ALERT.getName(), alert);
            LOGGER.error("Unable to create employer. Cause: {}", ex.getMessage());
            res.sendRedirect("/owner/add-employer");
        }
    }
}
