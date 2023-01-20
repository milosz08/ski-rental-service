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
import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.login.*;
import pl.polsl.skirentalservice.exception.*;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.LOGIN_PAGE_ALERT;

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
        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployer =
                    "SELECT e.password FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final String employerPassword = session.createQuery(jpqlFindEmployer, String.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                if (isNull(employerPassword)) throw new UserNotFoundException(reqDto);
                final BCrypt.Result result = BCrypt.verifyer().verify(reqDto.getPassword().toCharArray(), employerPassword);
                if (!result.verified) throw new InvalidCredentialsException(reqDto);

                final String jpqlSelectEmployer =
                    "SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(" +
                        "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName)," +
                        "IFNULL(e.imageUrl, 'static/images/default-profile-image.svg'), r.roleName, " +
                        "e.role.alias, e.role.roleEng, d.gender" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.role r " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final LoggedUserDataDto employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                session.getTransaction().commit();
                httpSession.setAttribute(LOGGED_USER_DETAILS.getName(), employer);
                LOGGER.info("Successful logged on {} account. Account data: {}", employer.getRoleEng(), employer);
                res.sendRedirect("/" + employer.getRoleEng() + "/dashboard");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) session.getTransaction().rollback();
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            req.setAttribute("alertData", alert);
            selfRedirect(req, res, resDto);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void selfRedirect(HttpServletRequest req, HttpServletResponse res, LoginFormResDto resDto)
        throws ServletException, IOException {
        req.setAttribute("loginData", resDto);
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }
}
