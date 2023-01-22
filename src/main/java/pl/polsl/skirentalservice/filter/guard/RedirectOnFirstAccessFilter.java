/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RedirectOnFirstAccessFilter.java
 *  Last modified: 21/01/2023, 16:20
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.http.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.ServletException;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.UserRole.OWNER;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = { "/first-access" }, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectOnFirstAccessFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final var userDetails = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        if (isNull(userDetails)) {
            res.sendRedirect("/login");
            return;
        }
        if (!userDetails.getIsFirstAccess() || userDetails.getRoleAlias().equals(OWNER.getAlias())) {
            final String redirect = userDetails.getRoleAlias().equals(OWNER.getAlias()) ? "owner" : "seller";
            res.sendRedirect("/" + redirect + "/dashboard");
            return;
        }
        chain.doFilter(req, res);
    }
}
