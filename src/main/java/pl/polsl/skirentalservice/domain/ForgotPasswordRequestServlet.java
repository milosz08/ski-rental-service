package pl.polsl.skirentalservice.domain;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordReqDto;
import pl.polsl.skirentalservice.dto.change_password.RequestToChangePasswordResDto;
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/forgot-password-request")
public class ForgotPasswordRequestServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final AuthService authService;

    @Inject
    public ForgotPasswordRequestServlet(
        ValidatorBean validatorBean,
        AuthService authService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.validatorBean = validatorBean;
        this.authService = authService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        req.addAttribute("alertData", req.getAlertAndDestroy(SessionAlert.FORGOT_PASSWORD_PAGE_ALERT));
        req.addAttribute("resetPassData", req.getFromSessionAndDestroy(this, RequestToChangePasswordResDto.class));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageOrRedirectTo("forgot-password-request")
            .pageTitle(PageTitle.FORGOT_PASSWORD_REQUEST_PAGE)
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final RequestToChangePasswordReqDto reqDto = new RequestToChangePasswordReqDto(req);
        final RequestToChangePasswordResDto resDto = new RequestToChangePasswordResDto(validatorBean, reqDto);

        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("forgot-password-request", this, resDto);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        try {
            authService.checkUserAndSendToken(reqDto, req);
            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Jeśli podany użytkownik istnieje, na adres email konta został przesłany link weryfikujący."
            );
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
        }
        req.setSessionAttribute(SessionAlert.FORGOT_PASSWORD_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo("forgot-password-request")
            .build();
    }

    @Override
    public String getAttributeName() {
        return getClass().getName();
    }
}
