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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/owner/edit-employer/*",
    "/owner/delete-employer/*",
    "/owner/employer-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenEmployerIdNotExistInPathFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String employerId = req.getParameter("id");
        if (!StringUtils.isNumeric(employerId)) {
            res.sendRedirect("/owner/employers");
            return;
        }
        chain.doFilter(req, res);
    }
}
