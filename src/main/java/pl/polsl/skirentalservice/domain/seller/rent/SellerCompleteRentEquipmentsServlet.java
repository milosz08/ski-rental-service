package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRedirectException;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.core.servlet.pageable.*;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.rent.AddEditEquipmentCartResDto;
import pl.polsl.skirentalservice.dto.rent.EquipmentRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/seller/complete-rent-equipments")
public class SellerCompleteRentEquipmentsServlet extends AbstractPageableWebServlet {
    private final RentService rentService;

    @Inject
    public SellerCompleteRentEquipmentsServlet(
        RentService rentService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.rentService = rentService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT);
        final InMemoryRentDataDto rentData = req.getAttribute("rentData", InMemoryRentDataDto.class);

        final AddEditEquipmentCartResDto editModal = req
            .getFromSessionAndDestroy(SessionAttribute.EQ_EDIT_CART_MODAL_DATA, AddEditEquipmentCartResDto.class);

        if (!rentData.isAllGood()) {
            throw new WebServletRedirectException("seller/customers");
        }
        try {
            final Slice<EquipmentRentRecordResDto> pageableRentEquipments = rentService
                .getPageableRentEquipments(pageable);
            rentService.calculatePricesForRentEquipments(pageableRentEquipments.elements(), rentData, editModal);

            req.addAttribute("pagesData", pageableRentEquipments.pagination());
            req.addAttribute("equipmentsData", pageableRentEquipments.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("addModalResDto", req.getFromSessionAndDestroy(SessionAttribute.EQ_ADD_CART_MODAL_DATA,
            AddEditEquipmentCartResDto.class));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.SELLER_CREATE_NEW_RENT_PAGE)
            .pageOrRedirectTo("seller/rent/seller-complete-rent-equipments")
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("e.id"),
            "name", new ServletSorterField("e.name"),
            "type", new ServletSorterField("t.name"),
            "countInStore", new ServletSorterField("e.countInStore"),
            "pricePerHour", new ServletSorterField("e.pricePerHour"),
            "priceForNextHour", new ServletSorterField("e.priceForNextHour"),
            "pricePerDay", new ServletSorterField("e.pricePerDay")
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
        return new PageableAttributes(
            SessionAttribute.RENT_EQUIPMENTS_LIST_SORTER,
            SessionAttribute.RENT_EQUIPMENTS_LIST_FILTER
        );
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return "seller/complete-rent-equipments";
    }
}
