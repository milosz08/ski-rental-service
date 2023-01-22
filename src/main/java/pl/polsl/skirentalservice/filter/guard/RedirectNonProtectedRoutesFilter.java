/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RedirectNonProtectedRoutesFilter.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.UserRole.OWNER;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = { "/login", "/forgot-password-request", "/change-forgotten-password/*" },
    initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectNonProtectedRoutesFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final var loggedUserDetailsDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        if (!isNull(loggedUserDetailsDto)) {
            final String redirect = loggedUserDetailsDto.getRoleAlias().equals(OWNER.getAlias()) ? "owner" : "seller";
            res.sendRedirect("/" + redirect + "/dashboard");
            return;
        }
        chain.doFilter(req, res);
    }
}