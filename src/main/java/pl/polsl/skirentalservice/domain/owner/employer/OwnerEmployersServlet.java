/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

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
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/owner/employers")
public class OwnerEmployersServlet extends AbstractPageableWebServlet {
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public OwnerEmployersServlet(
        OwnerEmployerService ownerEmployerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.ownerEmployerService = ownerEmployerService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT);
        try {
            final Slice<EmployerRecordResDto> pageableEmployers = ownerEmployerService.getPageableEmployers(pageable);
            req.addAttribute("pagesData", pageableEmployers.pagination());
            req.addAttribute("employersData", pageableEmployers.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_EMPLOYERS_PAGE)
            .pageOrRedirectTo("owner/employer/owner-employers")
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("e.id"),
            "fullName", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"),
            "pesel", new ServletSorterField("d.pesel"),
            "hiredDate", new ServletSorterField("e.hiredDate"),
            "email", new ServletSorterField("d.emailAddress"),
            "phoneNumber", new ServletSorterField("CONCAT('+', d.phoneAreaCode, ' ', d.phoneNumber)"),
            "gender", new ServletSorterField("d.gender")
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"),
            new FilterColumn("pesel", "Numerze PESEL", "d.pesel"),
            new FilterColumn("emailAddress", "Adresie email", "d.emailAddress"),
            new FilterColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber")
        );
    }

    @Override
    protected String defaultSorterColumn() {
        return "e.id";
    }

    @Override
    protected PageableAttributes setPageableAttributes() {
        return new PageableAttributes(SessionAttribute.EMPLOYERS_LIST_SORTER, SessionAttribute.EMPLOYERS_LIST_FILTER);
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return "owner/employers";
    }
}
