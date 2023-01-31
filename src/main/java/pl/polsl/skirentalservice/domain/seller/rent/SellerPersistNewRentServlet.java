/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SellerPersistNewRentServlet.java
 *  Last modified: 29/01/2023, 13:01
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.*;
import org.hibernate.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import pl.polsl.skirentalservice.entity.*;
import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ModelMapperBean;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

import java.util.*;
import java.io.IOException;

import static java.util.Objects.isNull;
import static java.lang.Integer.parseInt;
import static java.time.LocalDateTime.parse;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAlert.*;
import static pl.polsl.skirentalservice.util.AlertType.INFO;
import static pl.polsl.skirentalservice.util.RentStatus.RENTED;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/persist-new-rent")
public class SellerPersistNewRentServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerPersistNewRentServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ModelMapperBean modelMapper;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        if (isNull(rentData)) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);
        final var loggedEmployer = (LoggedUserDataDto) httpSession.getAttribute(LOGGED_USER_DETAILS.getName());

        try (final Session session = sessionFactory.openSession()) {
            if (rentData.getEquipments().isEmpty()) throw new AnyEquipmentsInCartNotFoundException();
            try {
                session.beginTransaction();

                final RentEntity rent = modelMapper.map(rentData, RentEntity.class);
                rent.setEquipments(new HashSet<>());

                final Set<RentEquipmentEntity> equipmentEntities = new HashSet<>();
                for (final CartSingleEquipmentDataDto cartData : rentData.getEquipments()) {
                    final String jpqlCheckEquipmentCount =
                        "SELECT e.availableCount FROM EquipmentEntity e WHERE e.id = :id";
                    final Integer eqCount = session.createQuery(jpqlCheckEquipmentCount, Integer.class)
                        .setParameter("id", cartData.getId())
                        .getSingleResult();
                    if (eqCount < parseInt(cartData.getCount())) throw new TooMuchEquipmentsException();

                    final RentEquipmentEntity equipment = modelMapper.map(cartData, RentEquipmentEntity.class);
                    final EquipmentEntity refEquipment = session.get(EquipmentEntity.class, cartData.getId());
                    equipment.setId(null);
                    equipment.setTotalPrice(cartData.getPriceUnits().getTotalPriceNetto());
                    equipment.setDepositPrice(cartData.getPriceUnits().getTotalDepositPriceNetto());
                    equipment.setEquipment(refEquipment);
                    equipment.setRent(rent);
                    equipmentEntities.add(equipment);

                    final String jpqlDecreaseAvailableEqCount =
                        "UPDATE EquipmentEntity e SET e.availableCount = e.availableCount - :rentedCount " +
                        "WHERE e.id = :eid";
                    session.createMutationQuery(jpqlDecreaseAvailableEqCount)
                        .setParameter("eid", cartData.getId())
                        .setParameter("rentedCount", cartData.getCount())
                        .executeUpdate();
                }
                rent.setId(null);
                rent.setIssuedDateTime(parse(rentData.getIssuedDateTime().replace(' ', 'T')));
                rent.setTotalPrice(rentData.getPriceUnits().getTotalPriceNetto());
                rent.setTotalDepositPrice(rentData.getPriceUnits().getTotalDepositPriceNetto());

                rent.setStatus(RENTED);
                rent.setCustomer(session.get(CustomerEntity.class, rentData.getCustomerId()));
                rent.setEmployer(session.get(EmployerEntity.class, loggedEmployer.getId()));
                rent.setEquipments(equipmentEntities);

                // TODO: wysyłanie wiadomości email
                // TODO: generowanie pdfa

                session.persist(rent);
                session.getTransaction().commit();
                alert.setType(INFO);
                alert.setMessage(
                    "Wypożyczenie o numerze <strong>" + rentData.getIssuedIdentifier() + "</strong> zostało pomyślnie " +
                    "złożone w systemie."
                );
                httpSession.setAttribute(COMMON_RENTS_PAGE_ALERT.getName(), alert);
                httpSession.removeAttribute(INMEMORY_NEW_RENT_DATA.getName());
                LOGGER.info("Successfuly persist new rent by: {} in database. Rent data: {}", loggedUser, rentData);
                res.sendRedirect("/seller/rents");
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            httpSession.setAttribute(SELLER_COMPLETE_RENT_PAGE_ALERT.getName(), alert);
            LOGGER.error("Failure persist new rent by: {} in database. Rent data: {}. Cause: {}", loggedUser, rentData,
                ex.getMessage());
            res.sendRedirect("/seller/complete-rent-equipments");
        }
    }
}
