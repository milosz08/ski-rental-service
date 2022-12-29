/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ProtectedRoutesGuardFilter.java
 *  Last modified: 28/12/2022, 03:43
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter;

import jakarta.servlet.http.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.annotation.*;
import jakarta.servlet.ServletException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.util.Objects;
import java.io.IOException;

import static pl.polsl.skirentalservice.util.UserRole.*;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

//----------------------------------------------------------------------------------------------------------------------

@WebFilter(urlPatterns = { "/seller/*", "/owner/*", "/login" }, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res,
                            FilterChain chain) throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final String path = req.getServletPath();
        final var loggedUserDetailsDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        if (Objects.isNull(loggedUserDetailsDto)) {
            if (!path.equals("/login")) {
                res.sendRedirect("/login");
                return;
            }
            chain.doFilter(req, res);
            return;
        }
        final Character userRole = loggedUserDetailsDto.getRoleAlias();
        if (userRole.equals(OWNER.getAlias()) && (path.startsWith("/seller/") || path.equals("/login"))) {
            res.sendRedirect("/owner/dashboard");
        } else if (userRole.equals(SELLER.getAlias()) && (path.startsWith("/owner/") || path.equals("/login"))) {
            res.sendRedirect("/seller/dashboard");
        }
        chain.doFilter(req, res);
    }
}
