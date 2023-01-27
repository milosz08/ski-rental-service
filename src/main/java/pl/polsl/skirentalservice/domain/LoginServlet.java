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
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;

import static java.util.Objects.isNull;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.CredentialException.*;
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
        final LogoutModalDto logoutModal = getFromSessionAndDestroy(req, LOGOUT_MODAL.getName(), LogoutModalDto.class);
        req.setAttribute("logoutModal", logoutModal);
        req.setAttribute("alertData", getAndDestroySessionAlert(req, LOGIN_PAGE_ALERT));
        req.setAttribute("loginData", getFromSessionAndDestroy(req, getClass().getName(), LoginFormResDto.class));
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final AlertTupleDto alert = new AlertTupleDto(true);
        final HttpSession httpSession = req.getSession();

        final LoginFormReqDto reqDto = new LoginFormReqDto(req);
        final LoginFormResDto resDto = new LoginFormResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            httpSession.setAttribute(getClass().getName(), resDto);
            res.sendRedirect("/login");
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployer =
                    "SELECT e.password FROM EmployerEntity e INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final String password = session.createQuery(jpqlFindEmployer, String.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                if (isNull(password)) throw new UserNotFoundException(reqDto, LOGGER);
                if (!(BCrypt.verifyer().verify(reqDto.getPassword().toCharArray(), password).verified)) {
                    throw new InvalidCredentialsException(reqDto, LOGGER);
                }

                final String jpqlSelectEmployer =
                    "SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(" +
                        "e.id, e.login, CONCAT(d.firstName, ' ', d.lastName)," +
                        "IFNULL(e.imageUrl, 'static/images/default-profile-image.svg'), r.roleName, " +
                        "e.role.alias, e.role.roleEng, d.gender, d.emailAddress, e.firstAccess" +
                    ") FROM EmployerEntity e " +
                    "INNER JOIN e.role r " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final LoggedUserDataDto employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                session.getTransaction().commit();
                httpSession.setAttribute(LOGGED_USER_DETAILS.getName(), employer);
                httpSession.removeAttribute(getClass().getName());
                LOGGER.info("Successful logged on {} account. Account data: {}", employer.getRoleEng(), employer);
                if (employer.getIsFirstAccess() && employer.getRoleAlias().equals(UserRole.SELLER.getAlias())) {
                    res.sendRedirect("/first-access");
                    return;
                }
                res.sendRedirect("/" + employer.getRoleEng() + "/dashboard");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(getClass().getName(), resDto);
            httpSession.setAttribute(LOGIN_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/login");
        }
    }
}
