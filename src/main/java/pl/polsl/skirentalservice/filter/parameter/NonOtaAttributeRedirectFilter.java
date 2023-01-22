/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: NonOtaAttributeRedirectFilter.java
 *  Last modified: 20/01/2023, 06:35
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.parameter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.util.Objects;
import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = "/change-forgotten-password/*", initParams = @WebInitParam(name = "mood", value = "awake"))
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
