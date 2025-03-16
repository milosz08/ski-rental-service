package pl.polsl.skirentalservice.domain.common.equipment;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.core.servlet.pageable.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.equipment.EquipmentRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.EquipmentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = {"/owner/equipments", "/seller/equipments"})
public class CommonEquipmentsServlet extends AbstractPageableWebServlet {
    private final EquipmentService equipmentService;

    @Inject
    public CommonEquipmentsServlet(
        EquipmentService equipmentService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.equipmentService = equipmentService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_EQUIPMENTS_PAGE_ALERT);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        try {
            final Slice<EquipmentRecordResDto> pageableEquipments = equipmentService.getPageableEquipments(pageable);
            req.addAttribute("pagesData", pageableEquipments.pagination());
            req.addAttribute("equipmentsData", pageableEquipments.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        final String pageParaphrase = new StringJoiner("/")
            .add(loggedUser.getRoleEng())
            .add("equipment")
            .add(loggedUser.getRoleEng() + "-equipments")
            .toString();
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.COMMON_EQUIPMENTS_PAGE)
            .pageOrRedirectTo(pageParaphrase)
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("e.id"),
            "name", new ServletSorterField("e.name"),
            "type", new ServletSorterField("t.name"),
            "countInStore", new ServletSorterField("e.availableCount"),
            "pricePerHour", new ServletSorterField("e.pricePerHour"),
            "priceForNextHour", new ServletSorterField("e.priceForNextHour"),
            "pricePerDay", new ServletSorterField("e.pricePerDay"),
            "valueCost", new ServletSorterField("e.valueCost")
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("name", "nazwie", "e.name"),
            new FilterColumn("type", "typie sprzętu", "t.name")
        );
    }

    @Override
    protected String defaultSorterColumn() {
        return "e.id";
    }

    @Override
    protected PageableAttributes setPageableAttributes() {
        return new PageableAttributes(SessionAttribute.EQUIPMENTS_LIST_SORTER, SessionAttribute.EQUIPMENTS_LIST_FILTER);
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return req.getLoggedUserRole() + "/equipments";
    }
}
