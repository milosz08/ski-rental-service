/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ProtectedRoutesGuardFilter.java
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

import org.apache.commons.lang3.StringUtils;

import pl.polsl.skirentalservice.util.UserRole;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebFilter(urlPatterns = {
    "/seller/*",
    "/owner/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws IOException, ServletException {
        final HttpSession httpSession = req.getSession();
        final LoggedUserDataDto userData = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        String redirectToPath = "";
        if (Objects.isNull(userData)) {
            redirectToPath = "/login";
        } else {
            if (userData.getRoleAlias().equals(UserRole.SELLER.getAlias()) && req.getRequestURI().contains("/owner")) {
                redirectToPath = "/seller/dashboard";
            } else if (userData.getRoleAlias().equals(UserRole.OWNER.getAlias()) && req.getRequestURI().contains("/seller")) {
                redirectToPath = "/owner/dashboard";
            } else if (userData.getIsFirstAccess() && userData.getRoleAlias().equals(UserRole.SELLER.getAlias())) {
                redirectToPath = "/first-access";
            }
        }
        if (!StringUtils.isBlank(redirectToPath)) {
            res.sendRedirect(redirectToPath);
            return;
        }
        chain.doFilter(req, res);
    }
}
