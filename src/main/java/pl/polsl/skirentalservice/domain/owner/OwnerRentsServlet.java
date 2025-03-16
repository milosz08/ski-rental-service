package pl.polsl.skirentalservice.domain.owner;

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
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/owner/rents")
public class OwnerRentsServlet extends AbstractPageableWebServlet {
    private final RentService rentService;

    @Inject
    public OwnerRentsServlet(
        RentService rentService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.rentService = rentService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_RENTS_PAGE_ALERT);
        try {
            final Slice<OwnerRentRecordResDto> pageableRents = rentService.getPageableOwnerRents(pageable);
            req.addAttribute("pagesData", pageableRents.pagination());
            req.addAttribute("rentsData", pageableRents.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.COMMON_RENTS_PAGE)
            .pageOrRedirectTo("owner/rent/owner-rents")
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("r.id"),
            "issuedIdentifier", new ServletSorterField("r.issuedIdentifier"),
            "issuedDateTime", new ServletSorterField("r.issuedDateTime"),
            "status", new ServletSorterField("r.status"),
            "totalPriceNetto", new ServletSorterField("r.totalPrice"),
            "totalPriceBrutto", new ServletSorterField("(r.tax / 100) * r.totalPrice + r.totalPrice"),
            "client", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"),
            "employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)")
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("issuedIdentifier", "Numerze wypożyczenia", "r.issuedIdentifier"),
            new FilterColumn("issuedDateTime", "Dacie stworzenia wypożyczenia", "CAST(r.issuedDateTime AS string)"),
            new FilterColumn("status", "Statusie wypożyczenia", "CAST(r.status AS string)"),
            new FilterColumn("client", "Po imieniu i nazwisku klienta", "CONCAT(d.firstName, ' ', d.lastName)"),
            new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)")
        );
    }

    @Override
    protected String defaultSorterColumn() {
        return "r.id";
    }

    @Override
    protected PageableAttributes setPageableAttributes() {
        return new PageableAttributes(SessionAttribute.RENTS_LIST_SORTER, SessionAttribute.RENTS_LIST_FILTER);
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return "owner/rents";
    }
}
