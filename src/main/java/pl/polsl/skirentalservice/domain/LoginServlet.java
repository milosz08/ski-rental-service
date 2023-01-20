/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoginServlet.java
 *  Last modified: 31/12/2022, 17:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import org.slf4j.*;
import org.hibernate.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.login.*;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;

import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    @EJB private HibernateBean database;
    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("alertData", Utils.getAndDestroySessionAlert(req, LOGIN_PAGE_ALERT));
        req.setAttribute("logoutModal", new LogoutModalDto(Utils.getAndDestroySessionBool(req, LOGOUT_MODAL)));
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final LoginFormReqDto reqDto = new LoginFormReqDto(req);
        final LoginFormResDto resDto = new LoginFormResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            selfRedirect(req, res, resDto);
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto();
        final Session session = database.open();
        final Transaction transaction = session.beginTransaction();

        final String jpqlFindEmployer =
            "SELECT e.password FROM EmployerEntity e " +
            "INNER JOIN e.userDetails d " +
            "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
        final String employerPassword = session.createQuery(jpqlFindEmployer, String.class)
                .setParameter("loginOrEmail", loginOrEmail)
                .getSingleResultOrNull();

        LoggedUserDataDto employer = null;
        if (!isNull(employerPassword)) {
            final BCrypt.Result result = BCrypt.verifyer().verify(reqDto.getPassword().toCharArray(), employerPassword);
            if (!result.verified) {
                alert.setActive(true);
                alert.setMessage("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
                LOGGER.warn("Attempt to login with invalid credentials. Credentials data: {}", reqDto);
            } else {
                final String jpqlSelectEmployer =
                    "SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(" +
                        "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName)," +
                        "IFNULL(e.imageUrl, 'static/images/default-profile-image.svg'), r.roleName, " +
                        "e.role.alias, e.role.roleEng, d.gender" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.role r " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
                        .setParameter("loginOrEmail", loginOrEmail)
                        .getSingleResultOrNull();
                final HttpSession httpSession = req.getSession();
                httpSession.setAttribute(LOGGED_USER_DETAILS.getName(), employer);
            }
        } else {
            alert.setActive(true);
            alert.setMessage("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            LOGGER.warn("Attempt to login on non existing account. Login data: {}", reqDto);
        }
        transaction.commit();
        session.close();

        if (isNull(employer)) {
            req.setAttribute("alertData", alert);
            selfRedirect(req, res, resDto);
            return;
        }
        LOGGER.info("Successful logged on {} account. Account data: {}", employer.getRoleEng(), employer);
        res.sendRedirect("/" + employer.getRoleEng() + "/dashboard");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, LoginFormResDto resDto)
        throws ServletException, IOException {
        req.setAttribute("loginData", resDto);
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }
}
