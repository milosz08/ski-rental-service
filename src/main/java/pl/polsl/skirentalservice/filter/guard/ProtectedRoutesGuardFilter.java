/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ProtectedRoutesGuardFilter.java
 *  Last modified: 27/01/2023, 15:27
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/seller/*",
    "/owner/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto userData = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        String redirectToPath = "";
        if (Objects.isNull(userData)) {
            redirectToPath = "/login";
        } else {
            if (userData.getRoleAlias().equals(UserRole.SELLER.getAlias()) && req.getRequestURI().contains("/owner")) {
                redirectToPath = "/seller/dashboard";
            } else if (userData.getRoleAlias().equals(UserRole.OWNER.getAlias()) && req.getRequestURI().contains("/seller")) {
                redirectToPath = "/owner/dashboard";
            } else if (userData.getIsFirstAccess() && userData.getRoleAlias().equals(UserRole.SELLER.getAlias())) {
                redirectToPath = "/first-access";
            }
        }
        if (!StringUtils.isBlank(redirectToPath)) {
            res.sendRedirect(redirectToPath);
            return;
        }
        chain.doFilter(req, res);
    }
}
