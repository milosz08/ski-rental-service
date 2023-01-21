/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerAddEmployerServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner.employer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.Map;
import java.io.IOException;

import pl.polsl.skirentalservice.dao.*;
import pl.polsl.skirentalservice.ssh.*;
import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.core.ssh.*;
import pl.polsl.skirentalservice.exception.*;
import pl.polsl.skirentalservice.core.mail.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Locale.ENGLISH;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.RandomStringUtils.*;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_ADD_EMPLOYER_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/owner/add-employer")
public class OwnerAddEmployerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerAddEmployerServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;
    @EJB private ModelMapperBean mapper;
    @EJB private MailSocketBean mailSocket;
    @EJB private SshSocketBean sshSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", OWNER_ADD_EMPLOYER_PAGE.getName());
        req.setAttribute("addEditText", "Dodaj");
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            selfRedirect(req, res, resDto, null);
            return;
        }
        final HttpSession httpSession = req.getSession();
        final IExecCommandPerformer commandPerformer = new ExecCommandPerformer(sshSocket);
        try (final Session session = database.open()) {
            if (reqDto.getBornDate().isAfter(reqDto.getHiredDate())) throw new EmployerBornHiredDateCollationException();
            String email = "";
            try {
                session.getTransaction().begin();

                final String jpqlFindPesel =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e INNER JOIN e.userDetails d WHERE d.pesel = :pesel";
                final Boolean peselExist = session.createQuery(jpqlFindPesel, Boolean.class)
                    .setParameter("pesel", reqDto.getPesel()).getSingleResult();
                if (peselExist) throw new PeselAlreadyExistException(reqDto.getPesel());

                final String jpqlFindPhoneNumber =
                    "SELECT COUNT(e.id) > 0 FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d WHERE d.phoneNumber = :phoneNumber";
                final Boolean phoneNumberExist = session.createQuery(jpqlFindPhoneNumber, Boolean.class)
                    .setParameter("phoneNumber", reqDto.getPhoneNumber()).getSingleResult();
                if (phoneNumberExist) throw new PhoneNumberAlreadyExistException(reqDto.getPhoneNumber());

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

                final UserDetailsEntity userDetails = mapper.map(reqDto, UserDetailsEntity.class);
                userDetails.setBornDate(reqDto.getBornDate());
                userDetails.setEmailAddress(email);
                final LocationAddressEntity locationAddress = mapper.map(reqDto, LocationAddressEntity.class);
                locationAddress.setApartmentNr(trimToNull(reqDto.getApartmentNr()));

                final EmployerEntity employer = new EmployerEntity();
                employer.setLogin(login);
                employer.setPassword(passowordDecoded);
                employer.setHiredDate(reqDto.getHiredDate());
                employer.setLocationAddress(locationAddress);
                employer.setUserDetails(userDetails);
                employer.setRole(role);

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
                httpSession.setAttribute(SessionAlert.EMPLOYERS_PAGE_ALERT.getName(), alert);
                res.sendRedirect("/owner/employers");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                if (!isEmpty(email)) commandPerformer.deleteMailbox(email);
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to create employer. Cause: {}", ex.getMessage());
            selfRedirect(req, res, resDto, alert);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, AddEditEmployerResDto resDto,
                              AlertTupleDto alert) throws ServletException, IOException {
        req.setAttribute("addEditEmployerData", resDto);
        req.setAttribute("addEditText", "Dodaj");
        req.setAttribute("alertData", alert);
        req.setAttribute("title", OWNER_ADD_EMPLOYER_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/employer/owner-add-edit-employer.jsp").forward(req, res);
    }
}
