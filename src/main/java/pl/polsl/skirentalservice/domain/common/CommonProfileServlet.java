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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = { "/seller/profile", "/owner/profile" })
public class CommonProfileServlet extends AbstractWebServlet {
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public CommonProfileServlet(
        OwnerEmployerService ownerEmployerService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.ownerEmployerService = ownerEmployerService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.COMMON_PROFILE_PAGE_ALERT);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        String redirectOrViewName = loggedUser.getRoleEng() + "/dashboard";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            req.addAttribute("employerData", ownerEmployerService.getEmployerFullDetails(loggedUser.getId()));

            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = new StringJoiner("/")
                .add(loggedUser.getRoleEng())
                .add(loggedUser.getRoleEng() + "-profile")
                .toString();
        } catch (AbstractAppException ex) {
            alert.setActive(true);
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.SELLER_DASHBOARD_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.SELLER_PROFILE_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
