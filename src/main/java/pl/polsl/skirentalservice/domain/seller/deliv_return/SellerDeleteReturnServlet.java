/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller.deliv_return;

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
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/delete-return")
public class SellerDeleteReturnServlet extends AbstractWebServlet {
    private final ReturnService returnService;

    @Inject
    public SellerDeleteReturnServlet(
        ReturnService returnService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.returnService = returnService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long returnId = req.getAttribute("returnId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            final String returnIdentifier = returnService.deleteReturn(returnId, loggedUser);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Usunięcie zwrotu wypożyczenia o numerze <strong>" + returnIdentifier +
                    "</strong> zakończone pomyślnie."
            );
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/returns")
            .build();
    }
}
