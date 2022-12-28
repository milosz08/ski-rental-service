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

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDetails;

import static pl.polsl.skirentalservice.util.UserRole.*;

//----------------------------------------------------------------------------------------------------------------------

@WebFilter(urlPatterns = { "/seller/*", "/owner/*", "/login" }, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res,
                            FilterChain chain) throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final String path = req.getServletPath();
        final LoggedUserDetails loggedUserDetails = (LoggedUserDetails) httpSession.getAttribute("logged_user_details");
        if (Objects.isNull(loggedUserDetails)) {
            if (!path.equals("/login")) res.sendRedirect("/login");
            chain.doFilter(req, res);
            return;
        }
        final Character userRole = loggedUserDetails.getRoleAlias();
        if (userRole.equals(OWNER.getAlias()) && (path.startsWith("/seller/") || path.equals("/login"))) {
            res.sendRedirect("/owner/dashboard");
        } else if (userRole.equals(SELLER.getAlias()) && (path.startsWith("/owner/") || path.equals("/login"))) {
            res.sendRedirect("/seller/dashboard");
        }
        chain.doFilter(req, res);
    }
}
