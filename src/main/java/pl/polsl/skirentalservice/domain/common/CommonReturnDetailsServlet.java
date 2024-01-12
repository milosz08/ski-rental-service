/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.common;

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
import pl.polsl.skirentalservice.dto.MultipleEquipmentsDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = { "/seller/return-details", "/owner/return-details" })
public class CommonReturnDetailsServlet extends AbstractWebServlet {
    private final ReturnService returnService;

    @Inject
    public CommonReturnDetailsServlet(
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

        String redirectOrViewName = loggedUser.getRoleEng() + "/returns";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            final MultipleEquipmentsDataDto<ReturnRentDetailsResDto> returnDetails = returnService
                .getReturnDetails(returnId, loggedUser);

            req.addAttribute("totalSum", returnDetails.totalCountOfRelatedElements());
            req.addAttribute("equipmentsReturnDetailsData", returnDetails.relatedElements());
            req.addAttribute("returnDetailsData", returnDetails.details());

            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = new StringJoiner("/")
                .add(loggedUser.getRoleEng())
                .add("deliv_return")
                .add(loggedUser.getRoleEng() + "-return-details")
                .toString();
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.COMMON_RETURN_DETAILS_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
