/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ProtectedRoutesGuardFilter.java
 *  Last modified: 22/01/2023, 11:41
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

import java.io.IOException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import static pl.polsl.skirentalservice.util.UserRole.*;
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
        String redirectToPath = "";
        if (isNull(userData)) {
            redirectToPath = "/login";
        } else {
            if (userData.getRoleAlias().equals(SELLER.getAlias()) && req.getRequestURI().contains("/owner")) {
                redirectToPath = "/seller/dashboard";
            } else if (userData.getRoleAlias().equals(OWNER.getAlias()) && req.getRequestURI().contains("/seller")) {
                redirectToPath = "/owner/dashboard";
            } else if (userData.getIsFirstAccess() && userData.getRoleAlias().equals(SELLER.getAlias())) {
                redirectToPath = "/first-access";
            }
        }
        if (!isBlank(redirectToPath)) {
            res.sendRedirect(redirectToPath);
            return;
        }
        chain.doFilter(req, res);
    }
}
