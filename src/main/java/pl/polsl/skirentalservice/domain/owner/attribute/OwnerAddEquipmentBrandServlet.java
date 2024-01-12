/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.attribute;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeValidatorPayloadDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.EquipmentAttributeService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/owner/add-equipment-brand")
public class OwnerAddEquipmentBrandServlet extends AbstractWebServlet {
    private final EquipmentAttributeService equipmentAttributeService;

    @Inject
    public OwnerAddEquipmentBrandServlet(
        EquipmentAttributeService equipmentAttributeService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.equipmentAttributeService = equipmentAttributeService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final String redirectUrl = req.getParameter("redirect", "/owner/add-equipment");
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AttributeValidatorPayloadDto payload = equipmentAttributeService.validateEquipmentAttribute(req);
        if (payload.isInvalid()) {
            req.setSessionAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA, payload.resDto());
            throw new WebServletRedirectException(redirectUrl);
        }
        final AttributeModalResDto res = payload.resDto();
        final AlertTupleDto alert = payload.alert();
        try {
            equipmentAttributeService.createNewEquipmentBrand(payload.reqDto().getName(), loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Nastąpiło pomyślne dodanie nowej marki sprzętu narciarskiego: <strong>" +
                    payload.reqDto().getName() + "</strong>."
            );
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Failure add new equipment brand by: {}. Cause: {}", loggedUser.getLogin(), ex.getMessage());
        }
        alert.setActive(true);
        res.setAlert(alert);
        res.setModalImmediatelyOpen(true);
        res.getName().setValue(StringUtils.EMPTY);

        req.setSessionAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA, payload.resDto());
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }
}
