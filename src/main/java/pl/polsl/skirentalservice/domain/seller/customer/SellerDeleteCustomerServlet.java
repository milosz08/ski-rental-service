/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.customer;

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
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/delete-customer")
public class SellerDeleteCustomerServlet extends AbstractWebServlet {
    private final CustomerService customerService;

    @Inject
    public SellerDeleteCustomerServlet(
        CustomerService customerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.customerService = customerService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);
        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            customerService.deleteCustomer(customerId, req);
            alert.setType(AlertType.INFO);
            alert.setMessage("Pomyślnie usunięto klienta z ID <strong>#" + customerId + "</strong> z systemu.");
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Unable to remove customer with id: {}. Cause: {}", customerId, ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/customers")
            .build();
    }
}
