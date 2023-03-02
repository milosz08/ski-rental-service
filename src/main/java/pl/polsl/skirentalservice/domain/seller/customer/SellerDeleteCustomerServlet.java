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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;

import pl.polsl.skirentalservice.util.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.core.db.HibernateUtil;
import pl.polsl.skirentalservice.core.mail.MailSocketBean;
import pl.polsl.skirentalservice.dao.customer.CustomerDao;
import pl.polsl.skirentalservice.dao.customer.ICustomerDao;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;
import pl.polsl.skirentalservice.dao.rent.RentDao;
import pl.polsl.skirentalservice.dao.rent.IRentDao;
import pl.polsl.skirentalservice.entity.CustomerEntity;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.entity.RentEquipmentEntity;

import static pl.polsl.skirentalservice.exception.NotFoundException.UserNotFoundException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.CustomerHasOpenedRentsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/delete-customer")
public class SellerDeleteCustomerServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerDeleteCustomerServlet.class);
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @EJB private MailSocketBean mailSocketBean;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final Long userId = NumberUtils.toLong(req.getParameter("id"));
        final AlertTupleDto alert = new AlertTupleDto(true);

        final HttpSession httpSession = req.getSession();
        try (final Session session = sessionFactory.openSession()) {
            try {
                session.beginTransaction();

                final IEquipmentDao equipmentDao = new EquipmentDao(session);
                final ICustomerDao customerDao = new CustomerDao(session);
                final IRentDao rentDao = new RentDao(session);

                final CustomerEntity customerEntity = session.get(CustomerEntity.class, userId);
                if (Objects.isNull(customerEntity)) throw new UserNotFoundException(UserRole.SELLER);
                if (customerDao.checkIfCustomerHasAnyActiveRents(userId)) {
                    throw new CustomerHasOpenedRentsException();
                }
                for (final RentEntity rentEntity : rentDao.findAllRentsBaseCustomerId(userId)) {
                    for (final RentEquipmentEntity equipment : rentEntity.getEquipments()) {
                        if (Objects.isNull(equipment.getEquipment())) continue;
                        equipmentDao.increaseAvailableSelectedEquipmentCount(equipment.getEquipment().getId(),
                            equipment.getCount());
                    }
                }
                final var rentData = (InMemoryRentDataDto) httpSession
                    .getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
                if (!Objects.isNull(rentData) && rentData.getCustomerId().equals(userId)) {
                    httpSession.removeAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
                }
                session.remove(customerEntity);
                session.getTransaction().commit();
                alert.setType(AlertType.INFO);
                alert.setMessage("Pomyślnie usunięto klienta z ID <strong>#" + userId + "</strong> z systemu.");
                LOGGER.info("Customer with id: {} was succesfuly removed from system.", userId);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            LOGGER.error("Unable to remove customer with id: {}. Cause: {}", userId, ex.getMessage());
        }
        httpSession.setAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT.getName(), alert);
        res.sendRedirect("/seller/customers");
    }
}
