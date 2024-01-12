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
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerResDto;
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/edit-customer")
public class SellerEditCustomerServlet extends AbstractWebServlet implements Attribute {
    private final CustomerService customerService;
    private final ServerConfigBean serverConfigBean;
    private final ValidatorBean validatorBean;

    @Inject
    public SellerEditCustomerServlet(
        CustomerService customerService,
        ServerConfigBean serverConfigBean,
        ValidatorBean validatorBean
    ) {
        super(serverConfigBean);
        this.customerService = customerService;
        this.serverConfigBean = serverConfigBean;
        this.validatorBean = validatorBean;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);

        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.SELLER_EDIT_CUSTOMER_PAGE_ALERT);
        AddEditCustomerResDto resDto = req.getFromSession(this, AddEditCustomerResDto.class);

        String redirectOrViewName = "seller/customers";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            if (resDto == null) {
                resDto = new AddEditCustomerResDto(validatorBean, customerService.getCustomerDetails(customerId));
            }
            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = "seller/customer/seller-add-edit-customer";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
        }
        req.addAttribute("addEditCustomerData", resDto);
        req.addAttribute("alertData", alert);
        req.addAttribute("addEditText", "Edytuj");
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.SELLER_EDIT_CUSTOMER_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("seller/edit-customer?id=" + customerId, this, resDto);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "seller/edit-customer?id=" + customerId;
        try {
            reqDto.validateMaturityAge(serverConfigBean);
            customerService.editCustomerDetails(reqDto, customerId);

            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Dane klienta z ID <strong>#" + customerId + "</strong> zostały pomyślnie zaktualizowane."
            );
            req.deleteSessionAttribute(this);
            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
            redirectUrl = "seller/customers";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.SELLER_EDIT_CUSTOMER_PAGE_ALERT, alert);
            log.error("Unable to edit existing customer with id: {}. Cause: {}", customerId, ex.getMessage());
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }

    @Override
    public String getAttributeName() {
        return getClass().getName();
    }
}
