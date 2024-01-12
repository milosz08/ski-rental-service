/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.UpdatedInMemoryRentData;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.dto.rent.NewRentDetailsReqDto;
import pl.polsl.skirentalservice.dto.rent.NewRentDetailsResDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.util.Objects;

@Slf4j
@WebServlet("/seller/create-new-rent")
public class SellerCreateNewRentServlet extends AbstractWebServlet implements Attribute {
    private final RentService rentService;
    private final ValidatorBean validatorBean;

    @Inject
    public SellerCreateNewRentServlet(
        ServerConfigBean serverConfigBean,
        RentService rentService,
        ValidatorBean validatorBean
    ) {
        super(serverConfigBean);
        this.rentService = rentService;
        this.validatorBean = validatorBean;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        NewRentDetailsResDto resDto = req.getFromSessionAndDestroy(this, NewRentDetailsResDto.class);
        var rentData = req.getFromSession(SessionAttribute.IN_MEMORY_NEW_RENT_DATA, InMemoryRentDataDto.class);

        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.SELLER_CREATE_NEW_RENT_PAGE_ALERT);
        if (rentData != null && !Objects.equals(rentData.getCustomerId(), customerId)) {
            persistAlertAndSendRedirect(req, alert, "W pamięci systemu istnieje już otwarte zgłoszenie wypożyczenia " +
                "dla klienta <a href='" + req.getContextPath() + "/seller/create-new-rent?id=" +
                rentData.getCustomerId() + "' class='alert-link'>" + rentData.getCustomerFullName() + "</a>. Aby " +
                "stworzyć nowe wypożyczenie, zamknij poprzednie lub usuń z pamięci systemu.");
        }
        try {
            final UpdatedInMemoryRentData data = rentService.updateAndGetInMemoryRentData(customerId, loggedUser);
            if (data.hasEmptyEquipments()) {
                persistAlertAndSendRedirect(req, alert, "Aby stworzyć wypożyczenie musi być dostępny przynajmniej " +
                    "jeden sprzęt w ilości jednej sztuki na stanie.");
            }
            if (rentData == null) {
                rentData = new InMemoryRentDataDto(customerId, data.customerDetails().fullName());
                rentData.setIssuedIdentifier(rentService.generateIssuedIdentifier(customerId, loggedUser.getId()));
                req.setSessionAttribute(SessionAttribute.IN_MEMORY_NEW_RENT_DATA, rentData);
            }
            rentData.setAllGood(false);
            rentData.setCustomerDetails(data.customerDetails());
            if (resDto == null) {
                resDto = new NewRentDetailsResDto();
                resDto.getTax().setValue(rentData.getTax());
                resDto.getRentDateTime().setValue(rentData.getRentDateTime());
                resDto.getReturnDateTime().setValue(rentData.getReturnDateTime());
                resDto.getDescription().setValue(rentData.getDescription());
            }
            resDto.setIssuedIdentifier(rentData.getIssuedIdentifier());
            resDto.setIssuedDateTime(rentData.getIssuedDateTime());
            resDto.setRentStatus(rentData.getRentStatus());

            req.addAttribute("customerData", data.customerDetails());
            req.addAttribute("employerData", data.employerDetails());
        } catch (AbstractAppException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("rentDetails", resDto);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.SELLER_CREATE_NEW_RENT_PAGE)
            .pageOrRedirectTo("seller/rent/seller-create-new-rent")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long customerId = req.getAttribute("customerId", Long.class);
        final var rentData = req.getFromSession(SessionAttribute.IN_MEMORY_NEW_RENT_DATA, InMemoryRentDataDto.class);

        final NewRentDetailsReqDto reqDto = new NewRentDetailsReqDto(req);
        final NewRentDetailsResDto resDto = new NewRentDetailsResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            req.setSessionAttribute(this, resDto);
            throw new WebServletRedirectException("seller/create-new-rent?id=" + customerId);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "seller/create-new-rent?id=" + customerId;
        try {
            rentService.persistFirstStageRentForm(reqDto, resDto, rentData, req.getLoggedUser(), customerId);

            alert.setType(AlertType.INFO);
            alert.setMessage("Podstawowe ustawienia nowego wypożyczenia zostały zapisane.");

            req.setSessionAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT, alert);
            redirectUrl = "seller/complete-rent-equipments";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.SELLER_CREATE_NEW_RENT_PAGE_ALERT, alert);
            req.setSessionAttribute(this, resDto);
            log.error("Unable to persist first stage rent form. Cause: {}", ex.getMessage());
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }

    private void persistAlertAndSendRedirect(WebServletRequest req, AlertTupleDto alert, String content) {
        alert.setActive(true);
        alert.setMessage(content);
        req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
        throw new WebServletRedirectException("seller/customers");
    }

    @Override
    public String getAttributeName() {
        return getClass().getName();
    }
}
