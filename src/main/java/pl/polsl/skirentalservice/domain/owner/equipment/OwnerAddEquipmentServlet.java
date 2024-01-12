/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.equipment;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentReqDto;
import pl.polsl.skirentalservice.dto.equipment.AddEditEquipmentResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.EquipmentAttributeService;
import pl.polsl.skirentalservice.service.EquipmentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/owner/add-equipment")
public class OwnerAddEquipmentServlet extends AbstractWebServlet implements Attribute {
    private final EquipmentAttributeService equipmentAttributeService;
    private final EquipmentService equipmentService;
    private final ValidatorBean validatorBean;

    @Inject
    public OwnerAddEquipmentServlet(
        EquipmentAttributeService equipmentAttributeService,
        EquipmentService equipmentService,
        ValidatorBean validatorBean,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.equipmentAttributeService = equipmentAttributeService;
        this.equipmentService = equipmentService;
        this.validatorBean = validatorBean;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.OWNER_ADD_EQUIPMENT_PAGE_ALERT);
        final AddEditEquipmentResDto resDto = req.getFromSessionOrCreate(this, AddEditEquipmentResDto.class);
        try {
            final var mergedAttributes = equipmentAttributeService.getMergedEquipmentAttributes();

            resDto.insertTypesSelects(mergedAttributes.get(SessionAttribute.EQ_TYPES_MODAL_DATA));
            resDto.insertBrandsSelects(mergedAttributes.get(SessionAttribute.EQ_BRANDS_MODAL_DATA));
            resDto.insertColorsSelects(mergedAttributes.get(SessionAttribute.EQ_COLORS_MODAL_DATA));

            for (final Map.Entry<SessionAttribute, List<FormSelectTupleDto>> entry : mergedAttributes.entrySet()) {
                req.addAttribute(entry.getKey().getAttributeName(), req.getModalAndDestroy(entry.getKey()));
            }
            req.addAttribute("addEditEquipmentData", resDto);
        } catch (AbstractAppException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("addEditText", "Dodaj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_ADD_EQUIPMENT_PAGE)
            .pageOrRedirectTo("owner/equipment/owner-add-edit-equipment")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEquipmentReqDto reqDto = new AddEditEquipmentReqDto(req);
        final AddEditEquipmentResDto resDto = new AddEditEquipmentResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("owner/add-equipment", this, resDto);
        }
        String redirectUrl = "owner/add-equipment";
        try {
            equipmentService.createNewEquipment(reqDto, loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Nastąpiło pomyślne zapisanie nowego sprzętu oraz wygenerowanie dla niego kodu kreskowego."
            );
            req.setSessionAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);
            redirectUrl = "owner/equipments";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.OWNER_ADD_EQUIPMENT_PAGE_ALERT, alert);
            log.error("Unable to create new equipment. Cause: {}", ex.getMessage());
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }

    @Override
    public String getAttributeName() {
        return getClass().getName();
    }
}
