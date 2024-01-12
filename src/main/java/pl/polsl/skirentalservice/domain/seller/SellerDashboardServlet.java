/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.seller;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@WebServlet("/seller/dashboard")
public class SellerDashboardServlet extends AbstractWebServlet {
    @Inject
    public SellerDashboardServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        req.addAttribute("alertData", req.getAlertAndDestroy(SessionAlert.SELLER_DASHBOARD_PAGE_ALERT));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.SELLER_DASHBOARD_PAGE)
            .pageOrRedirectTo("seller/seller-dashboard")
            .build();
    }
}
