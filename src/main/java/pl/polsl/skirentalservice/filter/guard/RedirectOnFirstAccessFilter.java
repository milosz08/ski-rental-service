package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.UserRole;

import java.io.IOException;

@WebFilter(urlPatterns = {
    "/first-access",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class RedirectOnFirstAccessFilter extends AbstractWebFilter {
    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        if (loggedUser == null) {
            req.sendRedirect("/login");
            return;
        }
        if (!loggedUser.getIsFirstAccess() || req.hasRole(UserRole.OWNER)) {
            req.sendRedirect("/" + loggedUser.getRoleEng() + "/dashboard");
            return;
        }
        continueRequest(req, chain);
    }
}
