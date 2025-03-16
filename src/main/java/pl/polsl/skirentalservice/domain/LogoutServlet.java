package pl.polsl.skirentalservice.domain;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/logout")
public class LogoutServlet extends AbstractWebServlet {
    @Inject
    public LogoutServlet(ServerConfigBean serverConfigBean) {
        super(serverConfigBean);
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        req.deleteSessionAttribute(SessionAttribute.LOGGED_USER_DETAILS);
        req.setSessionAttribute(SessionAttribute.LOGOUT_MODAL, new LogoutModalDto(true));
        log.info("Successful logout from user account. Account data: '{}'.", req.getLoggedUser());
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("login")
            .build();
    }
}
