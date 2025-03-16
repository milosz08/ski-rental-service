package pl.polsl.skirentalservice.filter.parameter;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.service.EquipmentService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/owner/edit-equipment/*",
    "/owner/delete-equipment/*",
    "/owner/equipment-details/*",
    "/seller/equipment-details/*",
    "/seller/add-equipment-to-cart/*",
    "/seller/delete-equipment-from-cart/*",
    "/seller/edit-equipment-from-cart/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class EquipmentIdNotExistFilter extends AbstractWebFilter {
    private final EquipmentService equipmentService;

    @Inject
    public EquipmentIdNotExistFilter(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String equipmentId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukany sprzęt narciarski nie istnieje.");

        if (!StringUtils.isNumeric(equipmentId) || !equipmentService.checkIfEquipmentExist(Long.parseLong(equipmentId))) {
            req.setSessionAttribute(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT, alert);
            req.sendRedirect("/" + req.getLoggedUserRole() + "/equipments");
            return;
        }
        req.addAttribute("equipmentId", Long.parseLong(equipmentId));
        continueRequest(req, chain);
    }
}
