/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: NotFoundServlet.java
 *  Last modified: 28/12/2022, 07:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.PageTitle.NOT_FOUND_4O4_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/404")
public class NotFoundServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", NOT_FOUND_4O4_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/_not-found.jsp").forward(req, res);
    }
}
