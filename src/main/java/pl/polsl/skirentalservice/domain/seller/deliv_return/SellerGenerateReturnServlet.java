package pl.polsl.skirentalservice.domain.seller.deliv_return;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.GeneratedReturnData;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/generate-return")
public class SellerGenerateReturnServlet extends AbstractWebServlet {
    private final ReturnService returnService;

    @Inject
    public SellerGenerateReturnServlet(
        ReturnService returnService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.returnService = returnService;
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long rentId = req.getAttribute("rentId", Long.class);
        final String description = StringUtils.trimToNull(req.getParameter("description"));
        final LoggedUserDataDto loggeduser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "seller/rents";
        try {
            final GeneratedReturnData returnData = returnService.generateReturn(rentId, description, loggeduser, req);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Generowanie zwrotu o numerze <strong>" + returnData.returnIdentifier() + "</strong> dla wypożyczenia o " +
                    "numerze <strong>" + returnData.rentIdentifier() + "</strong> zakończone sukcesem. Potwierdzenie " +
                    "wraz z podsumowaniem zostało wysłane również na adres email."
            );
            req.setSessionAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT, alert);
            redirectUrl = "seller/returns";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }
}
