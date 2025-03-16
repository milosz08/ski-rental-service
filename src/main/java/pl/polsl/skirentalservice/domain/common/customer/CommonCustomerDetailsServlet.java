package pl.polsl.skirentalservice.domain.common.customer;

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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = {"/seller/customer-details", "/owner/customer-details"})
public class CommonCustomerDetailsServlet extends AbstractWebServlet {
    private final CustomerService customerService;

    @Inject
    public CommonCustomerDetailsServlet(
        CustomerService customerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.customerService = customerService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);

        String redirectOrViewName = loggedUser.getRoleEng() + "/customers";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            req.addAttribute("customerData", customerService.getCustomerFullDetails(customerId));
            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = new StringJoiner("/")
                .add(loggedUser.getRoleEng())
                .add("customer")
                .add(loggedUser.getRoleEng() + "-customer-details")
                .toString();
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.COMMON_CUSTOMER_DETAILS_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
