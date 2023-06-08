/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RedirectWhenCustomerIdNotExistInPathFilter.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
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

import java.io.IOException;

import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
