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
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.service.CartEquipmentService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/seller/delete-equipment-from-cart")
public class SellerDeleteEquipmentFromCartServlet extends AbstractWebServlet {
    private final CartEquipmentService cartEquipmentService;

    @Inject
    public SellerDeleteEquipmentFromCartServlet(
        ServerConfigBean serverConfigBean,
        CartEquipmentService cartEquipmentService
    ) {
        super(serverConfigBean);
        this.cartEquipmentService = cartEquipmentService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long equipmentId = req.getAttribute("equipmentId", Long.class);
        final InMemoryRentDataDto rentData = req.getAttribute("rentData", InMemoryRentDataDto.class);

        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            cartEquipmentService.deleteEquipmentFromCart(rentData, equipmentId, loggedUser);

            alert.setType(AlertType.INFO);
            alert.setMessage("Pomyślnie usunięto pozycję z listy zestawienia sprzętów kreatora wypożyczania.");
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            log.error("Failure delete equipment from memory-persist data container by: {}. Cause: {}", loggedUser,
                ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.SELLER_COMPLETE_RENT_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("seller/complete-rent-equipments")
            .build();
    }
}
