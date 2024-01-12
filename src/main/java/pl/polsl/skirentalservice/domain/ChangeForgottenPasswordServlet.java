/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
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
import pl.polsl.skirentalservice.dto.change_password.ChangeForgottenPasswordReqDto;
import pl.polsl.skirentalservice.dto.change_password.ChangeForgottenPasswordResDto;
import pl.polsl.skirentalservice.dto.change_password.ChangePasswordEmployerDetailsDto;
import pl.polsl.skirentalservice.exception.CredentialException;
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/change-forgotten-password")
public class ChangeForgottenPasswordServlet extends AbstractWebServlet implements Attribute {
    private final AuthService authService;
    private final ValidatorBean validatorBean;

    @Inject
    public ChangeForgottenPasswordServlet(
        AuthService authService,
        ValidatorBean validatorBean,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.authService = authService;
        this.validatorBean = validatorBean;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final String token = req.getAttribute("token", String.class);
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.CHANGE_FORGOTTEN_PASSWORD_PAGE_ALERT);
        try {
            final ChangePasswordEmployerDetailsDto details = authService.getChangePasswordEmployerDetails(token, req);
            req.addAttribute("employerData", details);
        } catch (AbstractAppException ex) {
            alert.setActive(true);
            alert.setDisableContent(true);
            alert.setMessage(ex.getMessage());
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("changePassData", req.getFromSessionAndDestroy(this, ChangeForgottenPasswordResDto.class));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.CHANGE_FORGOTTEN_PASSWORD_PAGE)
            .pageOrRedirectTo("change-forgotten-password")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final String token = req.getAttribute("token", String.class);
        final AlertTupleDto alert = new AlertTupleDto(true);

        final ChangeForgottenPasswordReqDto reqDto = new ChangeForgottenPasswordReqDto(req);
        final ChangeForgottenPasswordResDto resDto = new ChangeForgottenPasswordResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("change-forgotten-password?token=" + token, this, resDto);
        }
        String redirectUrl = "change-forgotten-password?token=" + token;
        try {
            reqDto.validatePasswordsMatching();
            authService.changePassword(token, reqDto, req);
            alert.setMessage("Hasło do Twojego konta zostało pomyślnie zmienione.");
            alert.setType(AlertType.INFO);

            req.setSessionAttribute(SessionAlert.LOGIN_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);

            redirectUrl = "login";
        } catch (AbstractAppException ex) {
            if (!(ex instanceof CredentialException.PasswordMismatchException)) {
                alert.setDisableContent(true);
            }
            alert.setMessage(ex.getMessage());
        }
        req.setSessionAttribute(this, resDto);
        req.setSessionAttribute(SessionAlert.CHANGE_FORGOTTEN_PASSWORD_PAGE_ALERT, alert);
        return WebServletResponse.builder()
            .mode(HttpMethodMode.REDIRECT)
            .pageOrRedirectTo(redirectUrl)
            .build();
    }

    @Override
    public String getAttributeName() {
        return getClass().getName();
    }
}
