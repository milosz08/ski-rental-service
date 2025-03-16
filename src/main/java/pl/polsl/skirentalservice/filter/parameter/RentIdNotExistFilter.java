package pl.polsl.skirentalservice.filter.parameter;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/owner/rent-details/*",
    "/seller/rent-details/*",
    "/seller/generate-return/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RentIdNotExistFilter extends AbstractWebFilter {
    private final RentService rentService;

    @Inject
    public RentIdNotExistFilter(RentService rentService) {
        this.rentService = rentService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String rentId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukane wypożyczenie nie istnieje.");

        if (!StringUtils.isNumeric(rentId) || !rentService.checkIfRentExist(Long.parseLong(rentId))) {
            req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
            req.sendRedirect("/" + req.getLoggedUserRole() + "/rents");
            return;
        }
        req.addAttribute("rentId", Long.parseLong(rentId));
        continueRequest(req, chain);
    }
}
