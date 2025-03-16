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
import pl.polsl.skirentalservice.service.ReturnService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = {
    "/seller/return-details/*",
    "/seller/delete-return/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ReturnIsNotCreatedByEmployerFilter extends AbstractWebFilter {
    private final ReturnService returnService;

    @Inject
    public ReturnIsNotCreatedByEmployerFilter(ReturnService returnService) {
        this.returnService = returnService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String returnId = req.getParameter("id");
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Szukany zwrot wypożyczenia nie istnieje lub nie masz do niego dostępu.");

        if (!StringUtils.isNumeric(returnId)
            || !returnService.checkIfReturnIsFromEmployer(Long.parseLong(returnId), loggedUser.getId())) {
            req.setSessionAttribute(SessionAlert.COMMON_RETURNS_PAGE_ALERT, alert);
            req.sendRedirect("/seller/rents");
            return;
        }
        req.addAttribute("returnId", Long.parseLong(returnId));
        continueRequest(req, chain);
    }
}
