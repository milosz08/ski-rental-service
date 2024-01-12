/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.deliv_return;

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
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/seller/returns")
public class SellerReturnsServlet extends AbstractPageableWebServlet {
    private final ReturnService returnService;

    @Inject
    public SellerReturnsServlet(
        ReturnService returnService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.returnService = returnService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_RETURNS_PAGE_ALERT);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        try {
            final Slice<SellerRentReturnRecordResDto> pageableEmployerReturns = returnService
                .getPageableEmployerReturns(pageable, loggedUser.getId());
            req.addAttribute("pagesData", pageableEmployerReturns.pagination());
            req.addAttribute("returnsData", pageableEmployerReturns.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.COMMON_RETURNS_PAGE)
            .pageOrRedirectTo("seller/deliv_return/seller-returns")
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("r.id"),
            "issuedIdentifier", new ServletSorterField("r.issuedIdentifier"),
            "issuedDateTime", new ServletSorterField("r.issuedDateTime"),
            "totalPriceNetto", new ServletSorterField("r.totalPrice"),
            "totalPriceBrutto", new ServletSorterField("(rd.tax / 100) * r.totalPrice + r.totalPrice"),
            "rentIssuedIdentifier", new ServletSorterField("rd.issuedIdentifier")
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("issuedIdentifier", "Numerze zwrotu", "r.issuedIdentifier"),
            new FilterColumn("issuedDateTime", "Dacie stworzenia zwrotu", "CAST(r.issuedDateTime AS string)"),
            new FilterColumn("rentIssuedIdentifier", "Numerze wypożyczenia", "rd.issuedIdentifier")
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
        return "seller/returns";
    }
}
