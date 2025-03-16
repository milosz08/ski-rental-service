package pl.polsl.skirentalservice.filter.guard;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import pl.polsl.skirentalservice.core.servlet.AbstractWebFilter;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.UserRole;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

@WebFilter(urlPatterns = {
    "/seller/*",
    "/owner/*",
}, initParams = @WebInitParam(name = "mood", value = "awake"))
public class ProtectedRoutesGuardFilter extends AbstractWebFilter {
    @Override
    protected void doWebFilter(WebServletRequest req, FilterChain chain) throws IOException, ServletException {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        final String redirectToPath = AccountStatePredicate.determinateUrlBaseUserData(loggedUser, req);
        if (StringUtils.isBlank(redirectToPath)) {
            req.addAttribute("loggedUser", loggedUser);
            continueRequest(req, chain);
            return;
        }
        req.sendRedirect(redirectToPath);
    }

    @RequiredArgsConstructor
    private enum AccountStatePredicate {
        SELLER(UserRole.SELLER, (userData, uri) -> uri.contains("/owner"), "seller/dashboard"),
        OWNER(UserRole.OWNER, (userData, uri) -> uri.contains("/seller"), "owner/dashboard"),
        FIRST_ACCESS_SELLER(UserRole.SELLER, (userData, uri) -> userData.getIsFirstAccess(), "first-access"),
        ;

        private final UserRole role;
        private final BiFunction<LoggedUserDataDto, String, Boolean> accessPredicate;
        private final String redirectPath;

        public static String determinateUrlBaseUserData(LoggedUserDataDto userData, WebServletRequest req) {
            final HttpServletRequest request = req.getHttpReq();
            if (userData == null) {
                return "/login" + createRedirectUrl(request);
            }
            return Arrays.stream(values())
                .filter(a -> Objects.equals(a.role.getAlias(), userData.getRoleAlias()))
                .filter(a -> a.accessPredicate.apply(userData, request.getRequestURI()))
                .map(a -> "/" + a.redirectPath)
                .findFirst()
                .orElse(StringUtils.EMPTY);
        }

        private static String createRedirectUrl(HttpServletRequest request) {
            String redirectUrl = StringUtils.EMPTY;
            try {
                final URIBuilder uriBuilder = new URIBuilder(request.getRequestURI().substring(1));
                for (final Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                    uriBuilder.addParameter(entry.getKey(), entry.getValue()[0]);
                }
                redirectUrl = "?redirect=" + uriBuilder;
            } catch (URISyntaxException ignored) {
            }
            return redirectUrl;
        }
    }
}
