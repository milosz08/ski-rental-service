package pl.polsl.skirentalservice.filter.parameter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/seller/persist-new-rent/*",
    "/seller/reject-new-rent/*",
    "/seller/add-equipment-to-cart/*",
    "/seller/edit-equipment-from-cart/*",
    "/seller/delete-equipment-from-cart/*",
    "/seller/complete-rent-equipments/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class InMemoryRentDataFilter extends AbstractWebFilter {
    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final Object rentData = req.getFromSession(SessionAttribute.IN_MEMORY_NEW_RENT_DATA, InMemoryRentDataDto.class);
        final AlertTupleDto alert = new AlertTupleDto(true);

        if (rentData == null) {
            alert.setMessage("Nie istnieje w pamięci żadne zapisane wypożyczenie robocze.");
            req.setSessionAttribute(SessionAlert.COMMON_CUSTOMERS_PAGE_ALERT, alert);
            req.sendRedirect("/seller/customers");
            return;
        }
        req.addAttribute("rentData", rentData);
        continueRequest(req, chain);
    }
}
