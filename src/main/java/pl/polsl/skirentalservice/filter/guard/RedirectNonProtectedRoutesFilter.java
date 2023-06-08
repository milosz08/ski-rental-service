/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RedirectNonProtectedRoutesFilter.java
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

package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;

import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;

import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/login",
    "/forgot-password-request",
    "/change-forgotten-password/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectNonProtectedRoutesFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final var loggedUserDetailsDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        if (!Objects.isNull(loggedUserDetailsDto)) {
            res.sendRedirect("/" + loggedUserDetailsDto.getRoleEng() + "/dashboard");
            return;
        }
        chain.doFilter(req, res);
    }
}
