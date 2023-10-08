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
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/seller/edit-customer/*",
    "/seller/delete-customer/*",
    "/seller/customer-details/*",
    "/owner/customer-details/*",
    "/seller/create-new-rent/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectWhenCustomerIdNotExistInPathFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final String customerId = req.getParameter("id");
        final HttpSession httpSession = req.getSession();
        final var loggedUserDataDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        if (!StringUtils.isNumeric(customerId)) {
            res.sendRedirect("/" + loggedUserDataDto.getRoleEng() + "/customers");
            return;
        }
        chain.doFilter(req, res);
    }
}
