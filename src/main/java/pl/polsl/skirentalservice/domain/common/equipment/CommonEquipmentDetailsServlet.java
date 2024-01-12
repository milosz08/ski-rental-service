/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.common.equipment;

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
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = { "/owner/equipment-details", "/seller/equipment-details" })
public class CommonEquipmentDetailsServlet extends AbstractWebServlet {
    private final EquipmentService equipmentService;

    @Inject
    public CommonEquipmentDetailsServlet(
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

        String redirectOrViewName = loggedUser.getRoleEng() + "/equipments";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            req.addAttribute("equipmentData", equipmentService.getFullEquipmentDetails(equipmentId));
            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = new StringJoiner("/")
                .add(loggedUser.getRoleEng())
                .add("equipment")
                .add(loggedUser.getRoleEng() + "-equipment-details")
                .toString();
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.COMMON_EQUIPMENT_DETAILS_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
