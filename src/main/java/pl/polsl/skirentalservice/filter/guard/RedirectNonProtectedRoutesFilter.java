/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/login",
    "/forgot-password-request",
    "/change-forgotten-password/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectNonProtectedRoutesFilter extends AbstractWebFilter {
    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        if (loggedUser != null) {
            req.sendRedirect("/" + loggedUser.getRoleEng() + "/dashboard");
            return;
        }
        continueRequest(req, chain);
    }
}
