/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.util.PageTitle;

@WebServlet("/owner/dashboard")
public class OwnerDashboardServlet extends AbstractWebServlet {
    @Inject
    public OwnerDashboardServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_DASHBOARD_PAGE)
            .pageOrRedirectTo("owner/owner-dashboard")
            .build();
    }
}
