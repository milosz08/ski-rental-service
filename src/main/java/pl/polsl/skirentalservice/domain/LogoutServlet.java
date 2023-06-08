/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: LogoutServlet.java
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

package pl.polsl.skirentalservice.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServlet.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final HttpSession httpSession = req.getSession(false);
        final var detailsDto = (LoggedUserDataDto) httpSession.getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        httpSession.removeAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        httpSession.setAttribute(SessionAttribute.LOGOUT_MODAL.getName(), new LogoutModalDto(true));
        LOGGER.info("Successful logout from user account. Account data: {}", detailsDto);
        res.sendRedirect("/login");
    }
}
