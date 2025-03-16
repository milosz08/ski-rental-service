package pl.polsl.skirentalservice.domain.seller.rent;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/seller/reject-new-rent")
public class SellerRejectNewRentServlet extends AbstractWebServlet {
    @Inject
    public SellerRejectNewRentServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        final InMemoryRentDataDto inMemoryRentData = req.getAttribute("rentData", InMemoryRentDataDto.class);

        final AlertTupleDto alert = new AlertTupleDto(true);
        if (inMemoryRentData == null) {
            alert.setType(AlertType.WARN);
            alert.setMessage(
                "Usuwane robocze wypożyczenie zapisane w pamięci systemu nie zawierało żadnych zapisanych danych.");
        } else {
            alert.setType(AlertType.INFO);
            alert.setMessage("Pomyślnie usunięto z pamięci systemu nowe robocze wypożyczenie.");
            req.deleteSessionAttribute(SessionAttribute.IN_MEMORY_NEW_RENT_DATA);
            log.info("Successful removed temporary saved rent data by: {}. In memory data: {}", loggedUser,
                inMemoryRentData);
        }
        req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/customers")
            .build();
    }
}
