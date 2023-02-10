/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RedirectOnGetMethodRequestFilter.java
 *  Last modified: 25/01/2023, 14:25
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter.method;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/owner/add-equipment-type",
    "/owner/add-equipment-brand",
    "/owner/add-equipment-color",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectOnGetMethodRequestFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        if (req.getMethod().equals("GET")) {
            res.sendRedirect("/owner/add-equipment");
            return;
        }
        chain.doFilter(req, res);
    }
}
