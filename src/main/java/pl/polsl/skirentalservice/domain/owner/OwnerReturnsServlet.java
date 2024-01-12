/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
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
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/owner/returns")
public class OwnerReturnsServlet extends AbstractPageableWebServlet {
    private final ReturnService returnService;

    @Inject
    public OwnerReturnsServlet(
        ReturnService returnService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.returnService = returnService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_RETURNS_PAGE_ALERT);
        try {
            final Slice<OwnerRentReturnRecordResDto> pageableOwnerReturns = returnService
                .getPageableOwnerReturns(pageable);
            req.addAttribute("pagesData", pageableOwnerReturns.pagination());
            req.addAttribute("returnsData", pageableOwnerReturns.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.COMMON_RETURNS_PAGE)
            .pageOrRedirectTo("owner/deliv_return/owner-returns")
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("r.id"),
            "issuedIdentifier", new ServletSorterField("r.issuedIdentifier"),
            "issuedDatetime", new ServletSorterField("r.issuedDatetime"),
            "totalPriceNetto", new ServletSorterField("r.totalPrice"),
            "totalPriceBrutto", new ServletSorterField("(rd.tax / 100) * r.totalPrice + r.totalPrice"),
            "employer", new ServletSorterField("CONCAT(ed.firstName, ' ', ed.lastName)"),
            "rentIssuedIdentifier", new ServletSorterField("rd.issuedIdentifier")
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("issuedIdentifier", "Numerze zwrotu", "r.issuedIdentifier"),
            new FilterColumn("issuedDatetime", "Dacie stworzenia zwrotu", "CAST(r.issuedDatetime AS string)"),
            new FilterColumn("rentIssuedIdentifier", "Numerze wypożyczenia", "rd.issuedIdentifier"),
            new FilterColumn("employer", "Po imieniu i nazwisku pracownika", "CONCAT(ed.firstName, ' ', ed.lastName)")
        );
    }

    @Override
    protected String defaultSorterColumn() {
        return "r.id";
    }

    @Override
    protected PageableAttributes setPageableAttributes() {
        return new PageableAttributes(SessionAttribute.RETURNS_LIST_SORTER, SessionAttribute.RETURNS_LIST_FILTER);
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return "owner/returns";
    }
}
