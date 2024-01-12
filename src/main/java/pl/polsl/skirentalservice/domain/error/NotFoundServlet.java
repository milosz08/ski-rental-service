/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.error;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.util.PageTitle;

@WebServlet("/404")
public class NotFoundServlet extends AbstractWebServlet {
    @Inject
    public NotFoundServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.NOT_FOUND_4O4_PAGE)
            .pageOrRedirectTo("_not-found")
            .build();
    }
}
