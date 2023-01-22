/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerCustomerDetailsServlet.java
 *  Last modified: 21/01/2023, 16:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.customer;

import org.slf4j.*;
import org.hibernate.Session;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;

import java.io.IOException;

import static java.util.Objects.isNull;
import static pl.polsl.skirentalservice.util.PageTitle.OWNER_EMPLOYER_DETAILS_PAGE;
import static pl.polsl.skirentalservice.util.SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/customer-details")
public class SellerCustomerDetailsServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerCustomerDetailsServlet.class);

    @EJB private HibernateBean database;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                final String jpqlFindEmployerDetails =
                    "SELECT new pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto(" +
                        "c.id, CONCAT(d.firstName, ' ', d.lastName), d.emailAddress, CAST(d.bornDate AS string)," +
                        "d.pesel, CONCAT('+', d.phoneAreaCode, ' '," +
                        "SUBSTRING(d.phoneNumber, 1, 3), ' ', SUBSTRING(d.phoneNumber, 4, 3), ' '," +
                        "SUBSTRING(d.phoneNumber, 7, 3)), YEAR(NOW()) - YEAR(d.bornDate)," +
                        "d.gender, CONCAT(a.postalCode, ' ', a.city)," +
                        "CONCAT('ul. ', a.street, ' ', a.buildingNr, IF(a.apartmentNr, CONCAT('/', a.apartmentNr), ''))" +
                    ") FROM CustomerEntity c INNER JOIN c.userDetails d INNER JOIN c.locationAddress a " +
                    "WHERE c.id = :uid";
                final CustomerDetailsResDto customerDetails = session
                    .createQuery(jpqlFindEmployerDetails, CustomerDetailsResDto.class)
                    .setParameter("uid", userId)
                    .getSingleResultOrNull();
                if (isNull(customerDetails)) throw new NotFoundException.UserNotFoundException(userId);

                session.getTransaction().commit();
                req.setAttribute("customerData", customerDetails);
                req.setAttribute("title", OWNER_EMPLOYER_DETAILS_PAGE.getName());
                req.getRequestDispatcher("/WEB-INF/pages/seller/customer/seller-customer-details.jsp").forward(req, res);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(OWNER_EMPLOYERS_PAGE_ALERT.getName(), alert);
            res.sendRedirect("/owner/employers");
        }
    }
}
