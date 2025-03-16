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
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/change-forgotten-password/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class NonOtaAttributeFilter extends AbstractWebFilter {
    private final AuthService authService;

    @Inject
    public NonOtaAttributeFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final String token = req.getParameter("token");

        final AlertTupleDto alert = new AlertTupleDto(true);
        alert.setMessage("Podany token nie istnieje, został wykorzystany i/lub uległ przedawnieniu.");

        if (!StringUtils.isNumeric(token) || !authService.checkIfTokenIsExist(token)) {
            req.setSessionAttribute(SessionAlert.FORGOT_PASSWORD_PAGE_ALERT, alert);
            req.sendRedirect("/forgot-password-request");
            return;
        }
        req.addAttribute("token", token);
        continueRequest(req, chain);
    }
}
