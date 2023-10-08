/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.filter.parameter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

@WebFilter(urlPatterns = {
    "/change-forgotten-password/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class NonOtaAttributeRedirectFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String token = req.getParameter("token");
        if (Objects.isNull(token)) {
            res.sendRedirect("/forgot-password-request");
            return;
        }
        chain.doFilter(req, res);
    }
}
