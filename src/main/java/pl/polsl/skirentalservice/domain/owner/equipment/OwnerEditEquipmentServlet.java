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
@WebServlet("/owner/edit-equipment")
public class OwnerEditEquipmentServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final EquipmentService equipmentService;
    private final EquipmentAttributeService equipmentAttributeService;

    @Inject
    public OwnerEditEquipmentServlet(
        ValidatorBean validatorBean,
        EquipmentService equipmentService,
        EquipmentAttributeService equipmentAttributeService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.validatorBean = validatorBean;
        this.equipmentService = equipmentService;
        this.equipmentAttributeService = equipmentAttributeService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long equipmentId = req.getAttribute("equipmentId", Long.class);
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT);
        try {
            var resDto = req.getFromSession(this, AddEditEquipmentResDto.class);
            if (resDto == null) {
                resDto = new AddEditEquipmentResDto(validatorBean, equipmentService.getEquipmentDetails(equipmentId));
            }
            final var mergedAttributes = equipmentAttributeService.getMergedEquipmentAttributes();
            resDto.insertTypesSelects(mergedAttributes.get(SessionAttribute.EQ_TYPES_MODAL_DATA));
            resDto.insertBrandsSelects(mergedAttributes.get(SessionAttribute.EQ_BRANDS_MODAL_DATA));
            resDto.insertColorsSelects(mergedAttributes.get(SessionAttribute.EQ_COLORS_MODAL_DATA));

            for (final Map.Entry<SessionAttribute, List<FormSelectTupleDto>> entry : mergedAttributes.entrySet()) {
                req.addAttribute(entry.getKey().getAttributeName(), req.getModalAndDestroy(entry.getKey()));
            }
            req.addAttribute("addEditEquipmentData", resDto);
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT, alert);
        }
        req.addAttribute("equipmentId", String.valueOf(equipmentId));
        req.addAttribute("alertData", alert);
        req.addAttribute("addEditText", "Edytuj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_EDIT_EQUIPMENT_PAGE)
            .pageOrRedirectTo("owner/equipment/owner-add-edit-equipment")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long equipmentId = req.getAttribute("equipmentId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        final AddEditEquipmentReqDto reqDto = new AddEditEquipmentReqDto(req);
        final AddEditEquipmentResDto resDto = new AddEditEquipmentResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("owner/edit-equipment?id=" + equipmentId, this, resDto);
        }
        String redirectUrl = "owner/edit-equipment?id=" + equipmentId;
        try {
            equipmentService.editEquipment(equipmentId, reqDto, loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Pomyślnie dokonano edycji istniejącego sprzętu narciarskiego z ID <strong>#" + equipmentId +
                    "</strong>."
            );
            req.setSessionAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);
            redirectUrl = "owner/equipments";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.OWNER_EDIT_EQUIPMENT_PAGE_ALERT, alert);
            log.error("Unable to edit existing equipment with id: {}. Cause: {}", equipmentId, ex.getMessage());
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
