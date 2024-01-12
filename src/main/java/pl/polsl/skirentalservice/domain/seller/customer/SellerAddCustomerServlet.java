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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/add-customer")
public class SellerAddCustomerServlet extends AbstractWebServlet implements Attribute {
    private final CustomerService customerService;
    private final ServerConfigBean serverConfigBean;
    private final ValidatorBean validatorBean;

    @Inject
    public SellerAddCustomerServlet(
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
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.SELLER_ADD_CUSTOMER_PAGE_ALERT);
        final AddEditCustomerResDto resDto = req.getFromSessionOrCreate(this, AddEditCustomerResDto.class);

        req.addAttribute("alertData", alert);
        req.addAttribute("addEditCustomerData", resDto);
        req.addAttribute("addEditText", "Dodaj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.SELLER_ADD_CUSTOMER_PAGE)
            .pageOrRedirectTo("seller/customer/seller-add-edit-customer")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AddEditCustomerReqDto reqDto = new AddEditCustomerReqDto(req);
        final AddEditCustomerResDto resDto = new AddEditCustomerResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("seller/add-customer", this, resDto);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "seller/add-customer";
        try {
            reqDto.validateMaturityAge(serverConfigBean);
            customerService.createNewCustomer(reqDto, loggedUser);

            alert.setType(AlertType.INFO);
            alert.setMessage("Procedura dodawania nowego klienta do systemu zakończona sukcesem.");

            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);
            redirectUrl = "seller/customers";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.SELLER_ADD_CUSTOMER_PAGE_ALERT, alert);
            log.error("Unable to create new customer. Cause: {}", ex.getMessage());
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
