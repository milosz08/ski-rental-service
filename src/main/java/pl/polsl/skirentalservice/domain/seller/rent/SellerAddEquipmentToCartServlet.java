/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: SellerAddEquipmentToCartServlet.java
 *  Last modified: 07/02/2023, 13:11
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

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.dao.equipment.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.core.ValidatorBean;

import java.io.IOException;

import static java.util.Objects.isNull;
import static java.lang.Integer.parseInt;

import static pl.polsl.skirentalservice.util.Utils.*;
import static pl.polsl.skirentalservice.util.SessionAttribute.*;
import static pl.polsl.skirentalservice.exception.NotFoundException.*;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.*;
import static pl.polsl.skirentalservice.core.db.HibernateUtil.getSessionFactory;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@WebServlet("/seller/add-equipment-to-cart")
public class SellerAddEquipmentToCartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerAddEquipmentToCartServlet.class);
    private final SessionFactory sessionFactory = getSessionFactory();

    @EJB private ValidatorBean validator;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("/seller/complete-rent-equipments");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession.getAttribute(INMEMORY_NEW_RENT_DATA.getName());
        if (isNull(rentData)) {
            res.sendRedirect("/seller/customers");
            return;
        }
        final String equipmentId = req.getParameter("equipmentId");
        final String redirPag = req.getParameter("redirPag");
        final AlertTupleDto alert = new AlertTupleDto(true);
        final String loggedUser = getLoggedUserLogin(req);

        final AddEditEquipmentCartReqDto reqDto = new AddEditEquipmentCartReqDto(req);
        final AddEditEquipmentCartResDto resDto = new AddEditEquipmentCartResDto(validator, reqDto);
        if (validator.someFieldsAreInvalid(reqDto)) {
            resDto.setModalImmediatelyOpen(true);
            resDto.setEqId(equipmentId);
            httpSession.setAttribute(EQ_ADD_CART_MODAL_DATA.getName(), resDto);
            res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                final IEquipmentDao equipmentDao = new EquipmentDao(session);

                final var eqDetails = equipmentDao.findEquipmentDetails(equipmentId).orElseThrow(() -> {
                    throw new EquipmentNotFoundException(equipmentId);
                });
                if (rentData.getEquipments().stream().anyMatch(e -> e.getId().equals(eqDetails.getId()))) {
                    throw new EquipmentInCartAlreadyExistException();
                }
                if (eqDetails.getTotalCount() < parseInt(reqDto.getCount())) throw new TooMuchEquipmentsException();
                final CartSingleEquipmentDataDto cartData = new CartSingleEquipmentDataDto(eqDetails, reqDto, resDto);
                rentData.getEquipments().add(cartData);
                LOGGER.info("Successfuly add equipment to memory-persist data container by: {}. Data: {}", loggedUser,
                    cartData);
            } catch (RuntimeException ex) {
                onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            resDto.setModalImmediatelyOpen(true);
            resDto.setEqId(equipmentId);
            resDto.setAlert(alert);
            httpSession.setAttribute(EQ_ADD_CART_MODAL_DATA.getName(), resDto);
            LOGGER.error("Failure add equipment to memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
    }
}
