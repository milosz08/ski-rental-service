/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoginServlet.java
 *  Last modified: 25.12.2022, 02:37
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

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.login.*;
import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;

import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);

    @EJB private HibernateFactory database;
    @EJB private ValidatorFactory validator;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final boolean logoutModalVisible = !Objects.isNull(httpSession.getAttribute(LOGOUT_MODAL_VISIBLE.getName()));
        if (logoutModalVisible) {
            httpSession.removeAttribute(LOGOUT_MODAL_VISIBLE.getName());
        }
        req.setAttribute("logoutModal", new LogoutModalDto(logoutModalVisible));
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        final LoginFormReqDto reqDto = new LoginFormReqDto(login, password);
        final LoginFormResDto resDto = LoginFormResDto.builder()
                .login(validator.validateField(reqDto, "login", login))
                .password(validator.validateField(reqDto, "password"))
                .build();
        if (!validator.allFieldsIsValid(reqDto)) {
            redirectToLogin(req, res, resDto);
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto();
        final Session session = database.open();
        final Transaction transaction = session.beginTransaction();

        final String jpqlFindEmployer = "SELECT e.password FROM EmployerEntity e WHERE e.login = :login";
        final String employerPassword = session.createQuery(jpqlFindEmployer, String.class)
                .setParameter("login", login)
                .getSingleResultOrNull();

        LoggedUserDataDto employer = null;
        if (!Objects.isNull(employerPassword)) {
            final BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), employerPassword);
            if (!result.verified) {
                alert.setActive(true);
                alert.setMessage("Nieprawidłowe hasło. Spróbuj ponownie podając inne hasło.");
                LOGGER.warn("Attempt to login with invalid credentials. Credentials data: {}", reqDto);
            } else {
                final String jpqlSelectEmployer =
                        "SELECT new pl.polsl.skirentalservice.dto.login.LoggedUserDataDto(" +
                            "e.id, e.login, CONCAT(e.userDetails.firstName, ' ', e.userDetails.lastName), " +
                            "e.imageUrl, e.role.roleName, e.role.alias, e.userDetails.gender" +
                        ") FROM EmployerEntity e " +
                        "INNER JOIN e.role " +
                        "INNER JOIN e.userDetails d " +
                        "WHERE e.login = :login";
                employer = session.createQuery(jpqlSelectEmployer, LoggedUserDataDto.class)
                        .setParameter("login", login)
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

        if (Objects.isNull(employer)) {
            req.setAttribute("alertData", alert);
            redirectToLogin(req, res, resDto);
            return;
        }

        String roleType = UserRole.isSeller(employer.getRoleAlias()) ? "seller" : "owner";
        LOGGER.info("Successful logged on {} account. Account data: {}", roleType, employer);
        res.sendRedirect("/" + roleType + "/dashboard");
    }

    //------------------------------------------------------------------------------------------------------------------

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res, LoginFormResDto resDto)
            throws ServletException, IOException {
        req.setAttribute("loginData", resDto);
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }
}
