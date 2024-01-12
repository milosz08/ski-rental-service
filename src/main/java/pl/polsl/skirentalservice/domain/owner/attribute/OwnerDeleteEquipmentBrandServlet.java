/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.attribute;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.attribute.AttributeModalResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.EquipmentAttributeService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/owner/delete-equipment-brand")
public class OwnerDeleteEquipmentBrandServlet extends AbstractWebServlet {
    private final EquipmentAttributeService equipmentAttributeService;

    @Inject
    public OwnerDeleteEquipmentBrandServlet(
        EquipmentAttributeService equipmentAttributeService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.equipmentAttributeService = equipmentAttributeService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final String redirectUrl = req.getParameter("redirect", "/owner/add-equipment");
        final String brandId = req.getParameter("id");
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        final AttributeModalResDto resDto = new AttributeModalResDto();

        resDto.setAlert(alert);
        resDto.setModalImmediatelyOpen(true);
        try {
            final String deletedBrand = equipmentAttributeService.deleteEquipmentBrand(brandId, loggedUser);
            resDto.getActiveFirstPage().setActive(false);
            resDto.getActiveSecondPage().setActive(true);

            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Usuwanie marki sprzętu narciarskiego: <strong>" + deletedBrand + "</strong> zakończone sukcesem."
            );
        } catch (AbstractAppException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            log.error("Failure delete equipment brand by: {}. Cause: {}", loggedUser.getLogin(), ex.getMessage());
        }
        req.setSessionAttribute(SessionAttribute.EQ_BRANDS_MODAL_DATA, resDto);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }
}
