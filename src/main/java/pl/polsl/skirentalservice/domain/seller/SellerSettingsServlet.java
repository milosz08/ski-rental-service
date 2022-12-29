/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SettingsServlet.java
 *  Last modified: 29/12/2022, 00:38
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.PageTitle.SELLER_SETTINGS_PAGE;

//----------------------------------------------------------------------------------------------------------------------

@WebServlet("/seller/settings")
public class SellerSettingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", SELLER_SETTINGS_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/seller-settings.jsp").forward(req, res);
    }
}
