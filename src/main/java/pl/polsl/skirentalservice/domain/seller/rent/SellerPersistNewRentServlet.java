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
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.exception.NotFoundException;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/seller/persist-new-rent")
public class SellerPersistNewRentServlet extends AbstractWebServlet {
    private final RentService rentService;

    @Inject
    public SellerPersistNewRentServlet(
        ServerConfigBean serverConfigBean,
        RentService rentService
    ) {
        super(serverConfigBean);
        this.rentService = rentService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final InMemoryRentDataDto rentData = req.getAttribute("rentData", InMemoryRentDataDto.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "seller/complete-rent-equipments";
        try {
            if (rentData.getEquipments().isEmpty()) {
                throw new NotFoundException.AnyEquipmentsInCartNotFoundException();
            }
            rentService.persistNewRent(rentData, loggedUser, req);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Wypożyczenie o numerze <strong>" + rentData.getIssuedIdentifier() + "</strong> zostało pomyślnie " +
                    "złożone w systemie. Szczegóły złożonego wypożyczenia znajdziesz również w wiadomości email."
            );
            req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
            req.deleteSessionAttribute(SessionAttribute.IN_MEMORY_NEW_RENT_DATA);
            redirectUrl = "seller/rents";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT, alert);
            log.error("Failure persist new rent by: {} in database. Rent data: {}. Cause: {}", loggedUser, rentData,
                ex.getMessage());
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }
}
