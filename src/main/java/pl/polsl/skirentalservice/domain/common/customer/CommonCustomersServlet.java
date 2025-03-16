package pl.polsl.skirentalservice.domain.common.customer;

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
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.CustomerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = {"/seller/customers", "/owner/customers"})
public class CommonCustomersServlet extends AbstractPageableWebServlet {
    private final CustomerService customerService;

    private final String addressColumn =
        "CONCAT('ul. ', a.street, ' ', a.buildingNo, IF(a.apartmentNo, CONCAT('/', a.apartmentNo), '')," +
            "', ', a.postalCode, ' ', a.city)";

    @Inject
    public CommonCustomersServlet(
        CustomerService customerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.customerService = customerService;
    }

    @Override
    protected WebServletResponse onFetchPageableData(WebServletRequest req, PageableDto pageable) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        try {
            final Slice<CustomerRecordResDto> pageableCustomers = customerService
                .getPageableCustomers(pageable, addressColumn);
            req.addAttribute("pagesData", pageableCustomers.pagination());
            req.addAttribute("customersData", pageableCustomers.elements());
        } catch (AbstractAppException ex) {
            alert.setType(AlertType.ERROR);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        final String pageParaphrase = new StringJoiner("/")
            .add(loggedUser.getRoleEng())
            .add("customer")
            .add(loggedUser.getRoleEng() + "-customers")
            .toString();
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.COMMON_CUSTOMERS_PAGE)
            .pageOrRedirectTo(pageParaphrase)
            .build();
    }

    @Override
    protected Map<String, ServletSorterField> configureServletSorterFields() {
        return Map.of(
            "identity", new ServletSorterField("c.id"),
            "fullName", new ServletSorterField("CONCAT(d.firstName, ' ', d.lastName)"),
            "pesel", new ServletSorterField("d.pesel"),
            "email", new ServletSorterField("d.emailAddress"),
            "phoneNumber", new ServletSorterField("CONCAT('+', d.phoneAreaCode, ' ', d.phoneNumber)"),
            "address", new ServletSorterField(addressColumn)
        );
    }

    @Override
    protected List<FilterColumn> configureServletFilterFields() {
        return List.of(
            new FilterColumn("fullName", "Imieniu i nazwisku", "CONCAT(d.firstName, ' ', d.lastName)"),
            new FilterColumn("pesel", "Numerze PESEL", "d.pesel"),
            new FilterColumn("email", "Adresie email", "d.emailAddress"),
            new FilterColumn("phoneNumber", "Numerze telefonu", "d.phoneNumber"),
            new FilterColumn("address", "Adresie zamieszkania", addressColumn)
        );
    }

    @Override
    protected String defaultSorterColumn() {
        return "c.id";
    }

    @Override
    protected PageableAttributes setPageableAttributes() {
        return new PageableAttributes(SessionAttribute.CUSTOMERS_LIST_SORTER, SessionAttribute.CUSTOMERS_LIST_FILTER);
    }

    @Override
    protected String setRedirectOnPostCall(WebServletRequest req) {
        return req.getLoggedUserRole() + "/customers";
    }
}
