/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.error;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pl.polsl.skirentalservice.util.PageTitle;

import java.io.IOException;

@WebServlet("/403")
public class ForbiddenServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", PageTitle.FORBIDDEN_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/_forbidden.jsp").forward(req, res);
    }
}
