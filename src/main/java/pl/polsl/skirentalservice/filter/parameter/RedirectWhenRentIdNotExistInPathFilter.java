/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RedirectWhenRentIdNotExistInPathFilter.java
 *  Last modified: 30/01/2023, 22:53
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

import java.io.IOException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static org.apache.commons.lang3.StringUtils.isNumeric;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/owner/rent-details/*",
    "/seller/rent-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenRentIdNotExistInPathFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String rentId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var loggedUserDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());
        if (!isNumeric(rentId)) {
            res.sendRedirect("/" + loggedUserDataDto.getRoleEng() + "/rents");
            return;
        }
        chain.doFilter(req, res);
    }
}
