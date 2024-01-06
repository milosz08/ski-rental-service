/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.polsl.skirentalservice.core.ValidatorSingleton;
import pl.polsl.skirentalservice.core.db.HibernateDbSingleton;
import pl.polsl.skirentalservice.dao.EquipmentDao;
import pl.polsl.skirentalservice.dao.hibernate.EquipmentDaoHib;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartReqDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.CartSingleEquipmentDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.util.SessionAttribute;
import pl.polsl.skirentalservice.util.Utils;

import java.io.IOException;
import java.util.Objects;

import static pl.polsl.skirentalservice.exception.AlreadyExistException.EquipmentInCartAlreadyExistException;
import static pl.polsl.skirentalservice.exception.AlreadyExistException.TooMuchEquipmentsException;
import static pl.polsl.skirentalservice.exception.NotFoundException.EquipmentNotFoundException;

@WebServlet("/seller/add-equipment-to-cart")
public class SellerAddEquipmentToCartServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SellerAddEquipmentToCartServlet.class);

    private final SessionFactory sessionFactory = HibernateDbSingleton.getInstance().getSessionFactory();
    private final ValidatorSingleton validator = ValidatorSingleton.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendRedirect("/seller/complete-rent-equipments");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession();
        final var rentData = (InMemoryRentDataDto) httpSession
            .getAttribute(SessionAttribute.INMEMORY_NEW_RENT_DATA.getName());
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
            httpSession.setAttribute(SessionAttribute.EQ_ADD_CART_MODAL_DATA.getName(), resDto);
            res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
            return;
        }
        try (final Session session = sessionFactory.openSession()) {
            try {
                final EquipmentDao equipmentDao = new EquipmentDaoHib(session);

                final var eqDetails = equipmentDao.findEquipmentDetails(equipmentId)
                    .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
                if (rentData.getEquipments().stream().anyMatch(e -> e.getId().equals(eqDetails.getId()))) {
                    throw new EquipmentInCartAlreadyExistException();
                }
                if (eqDetails.getTotalCount() < Integer.parseInt(reqDto.getCount()))
                    throw new TooMuchEquipmentsException();
                final CartSingleEquipmentDataDto cartData = new CartSingleEquipmentDataDto(eqDetails, reqDto, resDto);
                rentData.getEquipments().add(cartData);
                LOGGER.info("Successfuly add equipment to memory-persist data container by: {}. Data: {}", loggedUser,
                    cartData);
            } catch (RuntimeException ex) {
                Utils.onHibernateException(session, LOGGER, ex);
            }
        } catch (RuntimeException ex) {
            alert.setMessage(ex.getMessage());
            resDto.setModalImmediatelyOpen(true);
            resDto.setEqId(equipmentId);
            resDto.setAlert(alert);
            httpSession.setAttribute(SessionAttribute.EQ_ADD_CART_MODAL_DATA.getName(), resDto);
            LOGGER.error("Failure add equipment to memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        res.sendRedirect("/seller/complete-rent-equipments" + redirPag);
    }
}
