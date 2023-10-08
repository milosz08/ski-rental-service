/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.UserRole;

import java.io.IOException;
import java.util.Objects;

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
