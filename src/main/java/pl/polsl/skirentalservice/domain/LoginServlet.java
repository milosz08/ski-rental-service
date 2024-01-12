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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.login.LoginFormReqDto;
import pl.polsl.skirentalservice.dto.login.LoginFormResDto;
import pl.polsl.skirentalservice.dto.logout.LogoutModalDto;
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.SessionAttribute;

@Slf4j
@WebServlet("/login")
public class LoginServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final AuthService authService;

    @Inject
    public LoginServlet(
        ValidatorBean validatorBean,
        AuthService authService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.validatorBean = validatorBean;
        this.authService = authService;
    }

    @Override
    public WebServletResponse httpGetCall(WebServletRequest req) {
        req.addAttribute("logoutModal", req.getFromSession(SessionAttribute.LOGOUT_MODAL, LogoutModalDto.class));
        req.addAttribute("alertData", req.getAlertAndDestroy(SessionAlert.LOGIN_PAGE_ALERT));
        req.addAttribute("loginData", req.getFromSessionAndDestroy(this, LoginFormResDto.class));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.LOGIN_PAGE)
            .pageOrRedirectTo("login")
            .build();
    }

    @Override
    public WebServletResponse httpPostCall(WebServletRequest req) {
        final String redirectPath = req.getParameter("redirect");
        final AlertTupleDto alert = new AlertTupleDto(true);

        final LoginFormReqDto reqDto = new LoginFormReqDto(req);
        final LoginFormResDto resDto = new LoginFormResDto(validatorBean, reqDto);

        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("login", this, resDto);
        }
        String redirectUrl;
        try {
            final LoggedUserDataDto employer = authService.loginUser(reqDto);

            req.setSessionAttribute(SessionAttribute.LOGGED_USER_DETAILS, employer);
            req.deleteSessionAttribute(this);

            redirectUrl = authService.isFirstAccessOrIsSellerAccount(employer)
                ? "first-access"
                : redirectPath == null ? employer.getRoleEng() + "/dashboard" : redirectPath;
        } catch (AbstractAppException ex) {
            redirectUrl = "login";
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.LOGIN_PAGE_ALERT, alert);
        }
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
