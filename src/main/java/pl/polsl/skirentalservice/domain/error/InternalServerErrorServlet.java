package pl.polsl.skirentalservice.domain.error;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.util.PageTitle;

@WebServlet("/500")
public class InternalServerErrorServlet extends AbstractWebServlet {
    @Inject
    public InternalServerErrorServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.INTERNAL_SERVER_ERROR_PAGE)
            .pageOrRedirectTo("_internal-server-error")
            .build();
    }
}
