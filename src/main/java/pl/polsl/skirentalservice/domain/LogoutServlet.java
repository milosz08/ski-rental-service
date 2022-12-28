/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LogoutServlet.java
 *  Last modified: 28/12/2022, 05:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import java.util.Objects;
import java.io.IOException;

import static pl.polsl.skirentalservice.util.SessionAttribute.*;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final HttpSession httpSession = req.getSession();
        if (!Objects.isNull(httpSession.getAttribute(LOGGED_USER_DETAILS.getName())) && !httpSession.isNew()) {
            httpSession.removeAttribute(LOGGED_USER_DETAILS.getName());
            httpSession.setAttribute(LOGOUT_MODAL_VISIBLE.getName(), true);
        }
        res.sendRedirect("/login");
    }
}
