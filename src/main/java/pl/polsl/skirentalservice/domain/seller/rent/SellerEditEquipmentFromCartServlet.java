/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartReqDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.service.CartEquipmentService;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/seller/edit-equipment-from-cart")
public class SellerEditEquipmentFromCartServlet extends AbstractWebServlet {
    private final CartEquipmentService cartEquipmentService;
    private final ValidatorBean validatorBean;

    @Inject
    public SellerEditEquipmentFromCartServlet(
        ServerConfigBean serverConfigBean,
        CartEquipmentService cartEquipmentService,
        ValidatorBean validatorBean
    ) {
        super(serverConfigBean);
        this.cartEquipmentService = cartEquipmentService;
        this.validatorBean = validatorBean;
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long equipmentId = req.getAttribute("equipmentId", Long.class);
        final InMemoryRentDataDto rentData = req.getAttribute("rentData", InMemoryRentDataDto.class);

        final String redirect = req.getParameter("redirect");
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AddEditEquipmentCartReqDto reqDto = new AddEditEquipmentCartReqDto(req);
        final AddEditEquipmentCartResDto resDto = new AddEditEquipmentCartResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            resDto.setModalImmediatelyOpen(true);
            resDto.setEquipmentId(equipmentId);
            req.setSessionAttribute(SessionAttribute.EQ_EDIT_CART_MODAL_DATA, resDto);
            throw new WebServletRedirectException("seller/complete-rent-equipments" + redirect);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            cartEquipmentService.editEquipmentFromCart(reqDto, resDto, rentData, loggedUser, equipmentId);
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            resDto.setModalImmediatelyOpen(true);
            resDto.setEquipmentId(equipmentId);
            resDto.setAlert(alert);
            req.setSessionAttribute(SessionAttribute.EQ_EDIT_CART_MODAL_DATA, resDto);
            log.error("Failure edit equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/complete-rent-equipments" + redirect)
            .build();
    }
}
