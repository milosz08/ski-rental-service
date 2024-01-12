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
import pl.polsl.skirentalservice.dto.first_access.FirstAccessReqDto;
import pl.polsl.skirentalservice.dto.first_access.FirstAccessResDto;
import pl.polsl.skirentalservice.service.AuthService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/first-access")
public class FirstAccessServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final AuthService authService;

    @Inject
    public FirstAccessServlet(
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
        req.addAttribute("alertData", req.getAlertAndDestroy(SessionAlert.FIRST_ACCESS_PAGE_ALERT));
        req.addAttribute("firstAccessData", req.getFromSessionAndDestroy(this, FirstAccessResDto.class));
        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.FIRST_ACCESS_PAGE)
            .pageOrRedirectTo("first-access")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final AlertTupleDto alert = new AlertTupleDto(true);

        final FirstAccessReqDto reqDto = new FirstAccessReqDto(req);
        final FirstAccessResDto resDto = new FirstAccessResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("first-access", this, resDto);
        }
        String redirectUrl = "first-access";
        try {
            reqDto.validatePasswordsMatching();
            authService.prepareEmployerAccount(reqDto, req.getLoggedUser());

            alert.setType(AlertType.INFO);
            alert.setMessage("Twoje nowe hasło do konta oraz do poczty zostało pomyślnie ustawione.");

            req.setSessionAttribute(SessionAlert.SELLER_DASHBOARD_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);

            redirectUrl = "seller/dashboard";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.FIRST_ACCESS_PAGE_ALERT, alert);
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
