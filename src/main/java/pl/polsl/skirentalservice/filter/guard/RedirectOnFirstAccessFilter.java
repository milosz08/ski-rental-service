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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.UserRole;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/first-access",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectOnFirstAccessFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final var userDetails = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        if (userDetails == null) {
            res.sendRedirect("/login");
            return;
        }
        if (!userDetails.getIsFirstAccess() || userDetails.getRoleAlias().equals(UserRole.OWNER.getAlias())) {
            res.sendRedirect("/" + userDetails.getRoleEng() + "/dashboard");
            return;
        }
        chain.doFilter(req, res);
    }
}
