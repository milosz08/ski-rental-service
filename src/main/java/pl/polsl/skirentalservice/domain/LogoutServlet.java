/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final HttpSession httpSession = req.getSession(false);
        final var detailsDto = (LoggedUserDataDto) httpSession
            .getAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        httpSession.removeAttribute(SessionAttribute.LOGGED_USER_DETAILS.getName());
        httpSession.setAttribute(SessionAttribute.LOGOUT_MODAL.getName(), new LogoutModalDto(true));
        LOGGER.info("Successful logout from user account. Account data: {}", detailsDto);
        res.sendRedirect("/login");
    }
}
