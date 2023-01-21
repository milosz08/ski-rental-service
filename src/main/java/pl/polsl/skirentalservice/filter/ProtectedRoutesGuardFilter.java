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

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.UserRole.SELLER;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = { "/seller/*", "/owner/*" },
    initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto userData = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        if (isNull(userData)) {
            res.sendRedirect("/login");
            return;
        }
        if (userData.getIsFirstAccess() && userData.getRoleAlias().equals(SELLER.getAlias())) {
            res.sendRedirect("/first-access");
            return;
        }
        chain.doFilter(req, res);
    }
}
