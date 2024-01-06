/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EmployerDao;
import pl.polsl.skirentalservice.dao.hibernate.EmployerDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.first_access.FirstAccessReqDto;
import pl.polsl.skirentalservice.dto.first_access.FirstAccessResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.ssh.ExecCommand;
import pl.polsl.skirentalservice.ssh.ExecCommandPerformer;
import pl.polsl.skirentalservice.util.*;

import java.io.IOException;

import static pl.polsl.skirentalservice.exception.CredentialException.PasswordMismatchException;

@Slf4j
@WebServlet("/first-access")
public class FirstAccessServlet extends HttpServlet {
    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("alertData", Utils.getAndDestroySessionAlert(req, SessionAlert.FIRST_ACCESS_PAGE_ALERT));
        req.setAttribute("firstAccessData", Utils.getFromSessionAndDestroy(req, getClass().getName(), FirstAccessResDto.class));
        req.setAttribute("title", PageTitle.FIRST_ACCESS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/first-access.jsp").forward(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());

        final FirstAccessReqDto reqDto = new FirstAccessReqDto(req);
        final FirstAccessResDto resDto = new FirstAccessResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/first-access");
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            if (!reqDto.getPassword().equals(reqDto.getPasswordRep())) {
                throw new PasswordMismatchException("nowe hasło do konta", "powtórz nowe hasło do konta");
            }
            if (!reqDto.getEmailPassword().equals(reqDto.getEmailPasswordRep())) {
                throw new PasswordMismatchException("nowe hasło do poczty", "powtórz nowe hasło do poczty");
            }
            try {
                session.beginTransaction();

                final EmployerDao employerDao = new EmployerDaoHib(session);
                employerDao.updateEmployerFirstAccessPassword(Utils.generateHash(reqDto.getPassword()), userDataDto.getId());

                final ExecCommand commandPerformer = new ExecCommandPerformer();
                commandPerformer.updateMailboxPassword(userDataDto.getEmailAddress(), reqDto.getEmailPassword());

                alert.setType(AlertType.INFO);
                alert.setMessage("Twoje nowe hasło do konta oraz do poczty zostało pomyślnie ustawione.");
                userDataDto.setIsFirstAccess(false);
                httpSession.setAttribute(SessionAlert.SELLER_DASHBOARD_PAGE_ALERT.getName(), alert);
                log.info("Successful changed default account password and mailbox password for user: {}", userDataDto);
                session.getTransaction().commit();
                httpSession.removeAttribute(getClass().getName());
                res.sendRedirect("/seller/dashboard");
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, log, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(SessionAlert.FIRST_ACCESS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/first-access");
        }
    }
}
