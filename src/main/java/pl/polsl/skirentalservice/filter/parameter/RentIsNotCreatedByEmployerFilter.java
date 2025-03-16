package pl.polsl.skirentalservice.filter.parameter;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {
    "/seller/rent-details/*",
    "/seller/delete-rent/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RentIsNotCreatedByEmployerFilter extends AbstractWebFilter {
    private final RentService rentService;

    @Inject
    public RentIsNotCreatedByEmployerFilter(RentService rentService) {
        this.rentService = rentService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String rentId = req.getParameter("id");
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukane wypożyczenie nie istnieje lub nie masz do niego dostępu.");

        if (!StringUtils.isNumeric(rentId)
            || !rentService.checkIfRentIsFromEmployer(Long.parseLong(rentId), loggedUser.getId())) {
            req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
            req.sendRedirect("/seller/rents");
            return;
        }
        req.addAttribute("rentId", Long.parseLong(rentId));
        continueRequest(req, chain);
    }
}
