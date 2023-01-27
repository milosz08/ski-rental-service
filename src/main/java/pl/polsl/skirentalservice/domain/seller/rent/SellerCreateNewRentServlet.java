/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerCreateNewRentServlet.java
 *  Last modified: 27/01/2023, 12:55
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.PageTitle.SELLER_CREATE_NEW_RENT_PAGE;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/create-new-rent")
public class SellerCreateNewRentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("title", SELLER_CREATE_NEW_RENT_PAGE.getName());
        req.getRequestDispatcher("/WEB-INF/pages/seller/rent/seller-create-new-rent.jsp").forward(req, res);
    }
}
