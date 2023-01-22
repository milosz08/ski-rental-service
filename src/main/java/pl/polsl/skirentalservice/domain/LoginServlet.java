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

import static pl.polsl.skirentalservice.util.AlertType.WARN;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;
import static pl.polsl.skirentalservice.util.Utils.invalidPassword;
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
        final HttpSession httpSession = req.getSession();
        boolean modalVisible = !isNull(httpSession.getAttribute(LOGOUT_MODAL.name()));
        if (modalVisible) httpSession.removeAttribute(LOGOUT_MODAL.name());
        req.setAttribute("logoutModal", new LogoutModalDto(modalVisible));
        req.setAttribute("alertData", Utils.getAndDestroySessionAlert(req, LOGIN_PAGE_ALERT));
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
            selfRedirect(req, res, resDto);
            return;
        }
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployer =
                    "SELECT new pl.polsl.skirentalservice.dto.login.PrependLoginDto(e.password, e.isBlocked) " +
                    "FROM EmployerEntity e " +
                    "INNER JOIN e.userDetails d " +
                    "WHERE e.login = :loginOrEmail OR d.emailAddress = :loginOrEmail";
                final PrependLoginDto prependLogin = session.createQuery(jpqlFindEmployer, PrependLoginDto.class)
                    .setParameter("loginOrEmail", reqDto.getLoginOrEmail())
                    .getSingleResultOrNull();

                if (isNull(prependLogin)) throw new UserNotFoundException(reqDto, LOGGER);
                if (prependLogin.getIsBlocked()) throw new AccountTemporaryBlockedException();
                if (invalidPassword(reqDto.getPassword(), prependLogin.getPassword())) {
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
                LOGGER.info("Successful logged on {} account. Account data: {}", employer.getRoleEng(), employer);
                if (employer.getIsFirstAccess() && employer.getRoleAlias().equals(UserRole.SELLER.getAlias())) {
                    res.sendRedirect("/first-access");
                    return;
                }
                res.sendRedirect("/" + employer.getRoleEng() + "/dashboard");
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (AccountTemporaryBlockedException ex) {
            alert.setType(WARN);
            alert.setMessage(ex.getMessage());
            req.setAttribute("alertData", alert);
            selfRedirect(req, res, resDto);
        } catch (RuntimeException ex) {
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
