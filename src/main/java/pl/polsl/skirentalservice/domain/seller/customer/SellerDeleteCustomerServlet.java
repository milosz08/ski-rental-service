/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerDeleteCustomerServlet.java
 *  Last modified: 31/01/2023, 06:34
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.customer;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dao.rent.*;
import pl.polsl.skirentalservice.dao.customer.*;
import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.mail.MailSocketBean;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;

import java.io.IOException;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.UserRole.SELLER;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.util.Utils.onHibernateException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;
import static pl.polsl.skirentalservice.util.SessionAttribute.INMEMORY_NEW_RENT_DATA;
import static pl.polsl.skirentalservice.util.SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-customer")
public class SellerDeleteCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteCustomerServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private MailSocketBean mailSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long userId = toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                final ICustomerDao customerDao = new CustomerDao(session);
                final IRentDao rentDao = new RentDao(session);

                final CustomerEntity customerEntity = session.get(CustomerEntity.class, userId);
                if (isNull(customerEntity)) throw new UserNotFoundException(SELLER);
                if (customerDao.checkIfCustomerHasAnyActiveRents(userId)) {
                    throw new CustomerHasOpenedRentsException();
                }
                for (final RentEntity rentEntity : rentDao.findAllRentsBaseCustomerId(userId)) {
                    for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                        if (isNull(equipment.getEquipment())) continue;
                        equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                            equipment.getCount());
                    }
                }
                final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
                if (!isNull(rentData) && rentData.getCustomerId().equals(userId)) {
                    httpSession.removeAttribute(INMEMORY_NEW_RENT_DATA.getName());
                }
                session.remove(customerEntity);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage("Pomyślnie usunięto klienta z ID <strong>#" + userId + "</strong> z systemu.");
                LOGGER.info("Customer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove customer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
