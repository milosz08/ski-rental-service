/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RedirectIncomigRootRequestFilter.java
 *  Last modified: 20/01/2023, 05:17
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

import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = "/", initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectIncomigRootRequestFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException {
        res.sendRedirect("/login");
    }
}
