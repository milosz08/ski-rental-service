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
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.EquipmentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/owner/delete-equipment")
public class OwnerDeleteEquipmentServlet extends AbstractWebServlet {
    private final EquipmentService equipmentService;

    @Inject
    public OwnerDeleteEquipmentServlet(
        EquipmentService equipmentService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.equipmentService = equipmentService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long equipmentId = req.getAttribute("equipmentId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            equipmentService.deleteEquipment(equipmentId, loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Pomyślnie usunięto sprzęt narciarski z ID <strong>#" + equipmentId + "</strong> z systemu."
            );
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Unable to remove equipment with id: {}. Cause: {}", equipmentId, ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("owner/equipments")
            .build();
    }
}
