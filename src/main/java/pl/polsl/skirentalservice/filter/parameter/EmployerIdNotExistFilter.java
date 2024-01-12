/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
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
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/owner/edit-employer/*",
    "/owner/delete-employer/*",
    "/owner/employer-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class EmployerIdNotExistFilter extends AbstractWebFilter {
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public EmployerIdNotExistFilter(OwnerEmployerService ownerEmployerService) {
        this.ownerEmployerService = ownerEmployerService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String employerId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukany pracownik nie istnieje.");

        if (!StringUtils.isNumeric(employerId) || !ownerEmployerService.checkIfEmployerExist(Long.parseLong(employerId))) {
            req.setSessionAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT, alert);
            req.sendRedirect("/owner/employers");
            return;
        }
        req.addAttribute("employerId", Long.parseLong(employerId));
        continueRequest(req, chain);
    }
}
