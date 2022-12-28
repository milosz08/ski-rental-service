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
import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.util.Objects;
import java.io.IOException;

import org.hibernate.Session;
import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.*;
import pl.polsl.skirentalservice.dto.login.*;
import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.dao.UserEntity;

import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGOUT_MODAL_VISIBLE;

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
        redirectToLogin(req, res, new AlertTupleDto(), new LoginFormResDto(), logoutModalVisible);
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

        final AlertTupleDto alert = new AlertTupleDto();
        if (!validator.allFieldsIsValid(reqDto)) {
            redirectToLogin(req, res, alert, resDto, false);
            return;
        }

        final Session session = database.open();
        final String query = "SELECT u FROM UserEntity u INNER JOIN FETCH u.role r " +
                             "WHERE u.login = :login AND NOT r.alias = 'U'";
        final UserEntity user = session.createQuery(query, UserEntity.class).setParameter("login", login)
                .getSingleResultOrNull();

        boolean redirectToLogin = false;
        if (!Objects.isNull(user)) {
            final BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (!result.verified) {
                alert.setActive(true);
                alert.setMessage("Nieprawidłowy login i/lub hasło. Spróbuj ponownie.");
                redirectToLogin = true;
                LOGGER.warn("Attempt to login with invalid credentials. User: {}", user);
            } else {
                final HttpSession httpSession = req.getSession();
                httpSession.setAttribute("logged_user_details", new LoggedUserDetails(user));
            }
        } else {
            alert.setActive(true);
            alert.setMessage("Użytkownik z podanymi danymi logowania nie istnieje w systemie.");
            redirectToLogin = true;
            LOGGER.warn("Attempt to login on non existing account. Login data: {}", reqDto);
        }

        session.close();
        if (redirectToLogin) {
            redirectToLogin(req, res, alert, resDto, false);
            return;
        }
        if (UserRole.isSeller(user.getRole())) {
            LOGGER.info("Successful logged on seller account. Account data: {}", user);
            res.sendRedirect("/seller/dashboard");
            return;
        }
        LOGGER.info("Successful logged on owner account. Account data: {}", user);
        res.sendRedirect("/owner/dashboard");
    }

    //------------------------------------------------------------------------------------------------------------------

    private void redirectToLogin(HttpServletRequest req, HttpServletResponse res, AlertTupleDto alert,
                                 LoginFormResDto resDto, boolean logoutModalVisible) throws ServletException, IOException {
        req.setAttribute("alertData", alert);
        req.setAttribute("loginData", resDto);
        req.setAttribute("logoutModalVisible", logoutModalVisible);
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }
}
