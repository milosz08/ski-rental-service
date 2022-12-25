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

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.core.HibernateFactory;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;
import static pl.polsl.skirentalservice.util.PageTitle.LOGIN_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB private HibernateFactory factory;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", LOGIN_PAGE.getName());
        req.setAttribute("loginData", new LoginFormDto());
        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String login = req.getParameter("login");
        final String password = req.getParameter("password");

        final LoginFormDto loginFormDto = LoginFormDto.builder()
                .login(new FormValueInfoTupleDto(login, "is-invalid", "Nieprawidłowy login/email."))
                .password(new FormValueInfoTupleDto("", "is-invalid", "Nieprawidłowe hasło."))
                .banner(new AlertTupleDto(true, "Użytkownik z podanymi danymi logowania nie istnieje.", ERROR))
                .build();

        req.setAttribute("loginData", loginFormDto);
        req.setAttribute("title", LOGIN_PAGE.getName());

        req.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(req, res);
    }
}
