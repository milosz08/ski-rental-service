/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ProtectedRoutesGuardFilter.java
 *  Last modified: 28/12/2022, 03:43
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.util.Objects;
import java.io.IOException;

import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = { "/seller/*", "/owner/*" }, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        if (Objects.isNull(httpSession.getAttribute(LOGGED_USER_DETAILS.getName()))) {
            res.sendRedirect("/login");
            return;
        }
        chain.doFilter(req, res);
    }
}
