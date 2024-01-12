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
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/delete-rent")
public class SellerDeleteRentServlet extends AbstractWebServlet {
    private final RentService rentService;

    @Inject
    public SellerDeleteRentServlet(RentService rentService, ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
        this.rentService = rentService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long rentId = req.getAttribute("rentId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            final String rentIdentifier = rentService.deleteRent(rentId, loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Pomyślnie usunięto wypożyczenie <strong>" + rentIdentifier + "</strong> " +
                    "z systemu."
            );
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Unable to remove rent with id: {} by {}. Cause: {}", rentId, loggedUser, ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/rents")
            .build();
    }
}
