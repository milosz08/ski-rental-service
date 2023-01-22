/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerDeleteCustomerServlet.java
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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.db.HibernateBean;
import pl.polsl.skirentalservice.core.mail.MailSocketBean;

import java.io.IOException;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.SessionAlert.SELLER_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-customer")
public class SellerDeleteCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteCustomerServlet.class);

    @EJB private HibernateBean database;
    @EJB private MailSocketBean mailSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String userId = req.getParameter("id");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = database.open()) {
            try {
                session.beginTransaction();

                // TODO: sprawdzanie, czy klient nie ma aktywnych wypożyczeń przedmiotów, jeśli tak rzucenie wyjątku
                // TODO: usuwanie klienta z systemu, hibernate (relacja kaskadowa, zamówienie NONE)

                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage("Pomyślnie usunięto klienta z ID <strong>#" + userId + "</strong> z systemu.");
                LOGGER.info("Customer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                if (session.getTransaction().isActive()) {
                    LOGGER.error("Some issues appears. Transaction rollback and revert previous state...");
                    session.getTransaction().rollback();
                }
                throw ex;
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove customer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(SELLER_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
