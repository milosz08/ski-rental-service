/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: OwnerDashboardServlet.java
 *  Last modified: 28/12/2022, 02:10
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.owner;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.PageTitle.OWNER_DASHBOARD_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/owner/dashboard")
public class OwnerDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", OWNER_DASHBOARD_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/owner/owner-dashboard.jsp").forward(req, res);
    }
}
