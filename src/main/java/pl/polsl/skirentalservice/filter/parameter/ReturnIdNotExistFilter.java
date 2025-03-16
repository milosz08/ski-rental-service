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
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/seller/return-details/*",
    "/owner/return-details/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ReturnIdNotExistFilter extends AbstractWebFilter {
    private final ReturnService returnService;

    @Inject
    public ReturnIdNotExistFilter(ReturnService returnService) {
        this.returnService = returnService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String returnId = req.getParameter("id");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukany zwrot wypożyczenia nie istnieje.");

        if (!StringUtils.isNumeric(returnId) || !returnService.checkIfReturnExist(Long.parseLong(returnId))) {
            req.setSessionAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT, alert);
            req.sendRedirect("/" + req.getLoggedUserRole() + "/rents");
            return;
        }
        req.addAttribute("returnId", Long.parseLong(returnId));
        continueRequest(req, chain);
    }
}
