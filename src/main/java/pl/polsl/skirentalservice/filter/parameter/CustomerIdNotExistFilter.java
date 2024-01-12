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
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/seller/edit-customer/*",
    "/seller/delete-customer/*",
    "/seller/customer-details/*",
    "/seller/create-new-rent/*",
    "/owner/customer-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class CustomerIdNotExistFilter extends AbstractWebFilter {
    private final CustomerService customerService;

    @Inject
    public CustomerIdNotExistFilter(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String customerId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukany klient nie istnieje.");

        if (!StringUtils.isNumeric(customerId) || !customerService.checkIfCustomerExist(Long.parseLong(customerId))) {
            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
            req.sendRedirect("/" + req.getLoggedUserRole() + "/customers");
            return;
        }
        req.addAttribute("customerId", Long.parseLong(customerId));
        continueRequest(req, chain);
    }
}
