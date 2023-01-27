/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RentsServlet.java
 *  Last modified: 27/01/2023, 13:26
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.common.rent;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import static pl.polsl.skirentalservice.util.PageTitle.COMMON_RENT_DETAILS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAttribute.LOGGED_USER_DETAILS;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet(urlPatterns = { "/seller/rent-details", "/owner/rent-details" })
public class CommonRentDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var userDataDto = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        // TODO: tutaj kod ze szczegółami wybranego zamówienia na podstawie ID

        req.setAttribute("title", COMMON_RENT_DETAILS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/" + userDataDto.getRoleEng() + "/rent/" +
            userDataDto.getRoleEng() + "-rent-details.jsp").forward(req, res);
    }
}
