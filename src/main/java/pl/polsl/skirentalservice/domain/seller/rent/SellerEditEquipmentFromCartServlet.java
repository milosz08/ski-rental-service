/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerEditEquipmentFromCartServlet.java
 *  Last modified: 30/01/2023, 20:27
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.domain.seller.rent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;
import java.io.IOException;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Utils;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartReqDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.equipment.EquipmentDao;
import pl.polsl.skirentalservice.dao.equipment.IEquipmentDao;

import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentInCartNotFoundException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.TooMuchEquipmentsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/edit-equipment-from-cart")
public class SellerEditEquipmentFromCartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerEditEquipmentFromCartServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("/seller/complete-rent-equipments");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
        if (Objects.isNull(rentData)) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final String equipmentId = req.getParameter("equipmentId");
        final String redirPag = req.getParameter("redirPag");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = Utils.getLoggedUserLogin(req);

        final AddEditEquipmentCartReqDto reqDto = new AddEditEquipmentCartReqDto(req);
        final AddEditEquipmentCartResDto resDto = new AddEditEquipmentCartResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            resDto.setModalImmediatelyOpen(true);
            resDto.setEqId(equipmentId);
            httpSession.setAttribute(SessionAttribute.EQ_EDIT_CART_MODAL_DATA.getName(), resDto);
            res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final var equipmentDetails = equipmentDao.findEquipmentDetails(equipmentId)
                    .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
                final CartSingleEquipmentDataDto cartData = rentData.getEquipments().stream()
                    .filter(e -> e.getId().equals(equipmentDetails.getId())).findFirst()
                    .orElseThrow(EquipmentInCartNotFoundException::new);
                if (equipmentDetails.getTotalCount() < Integer.parseInt(reqDto.getCount())) {
                    throw new TooMuchEquipmentsException();
                }
                cartData.setCount(reqDto.getCount());
                cartData.setDescription(reqDto.getDescription());
                if (!reqDto.getDepositPrice().isEmpty()) {
                    cartData.getPriceUnits().setTotalDepositPriceNetto(new BigDecimal(reqDto.getDepositPrice()));
                }
                cartData.setResDto(resDto);
                LOGGER.info("Successfuly edit equipment from memory-persist data container by: {}. Data: {}", loggedUser,
                    cartData);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            resDto.setModalImmediatelyOpen(true);
            resDto.setEqId(equipmentId);
            resDto.setAlert(alert);
            httpSession.setAttribute(SessionAttribute.EQ_EDIT_CART_MODAL_DATA.getName(), resDto);
            LOGGER.error("Failure edit equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
    }
}
