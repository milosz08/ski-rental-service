/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.domain.owner.employer;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.core.servlet.*;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

@Slf4j
@WebServlet("/owner/add-employer")
public class OwnerAddEmployerServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final ServerConfigBean serverConfigBean;
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public OwnerAddEmployerServlet(
        ValidatorBean validatorBean,
        ServerConfigBean serverConfigBean,
        OwnerEmployerService ownerEmployerService
    ) {
        super(serverConfigBean);
        this.validatorBean = validatorBean;
        this.serverConfigBean = serverConfigBean;
        this.ownerEmployerService = ownerEmployerService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final AddEditEmployerResDto resDto = req.getFromSessionOrCreate(this, AddEditEmployerResDto.class);

        req.addAttribute("alertData", req.getAlertAndDestroy(SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT));
        req.addAttribute("addEditEmployerData", resDto);
        req.addAttribute("addEditText", "Dodaj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_ADD_EMPLOYER_PAGE)
            .pageOrRedirectTo("owner/employer/owner-add-edit-employer")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validatorBean, reqDto);

        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("owner/add-employer", this, resDto);
        }
        String redirectUrl = "owner/add-employer";
        try {
            reqDto.validateDates(serverConfigBean);
            final String emailAddress = ownerEmployerService.addEmplyerByOwner(reqDto, req.getLoggedUser(), req);
            alert.setMessage(
                "Nastąpiło pomyślnie dodanie nowego pracownika. Na adres email <strong>" + emailAddress + "</strong> " +
                    "zostało wysłane hasło dostępu do konta. Hasło dostępu do skrzynki email użytkownika znajdziesz " +
                    "w przysłanej na Twój adres email wiadomości."
            );
            alert.setType(AlertType.INFO);
            req.setSessionAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT, alert);
            req.deleteSessionAttribute(this);
            redirectUrl = "owner/employers";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.OWNER_ADD_EMPLOYER_PAGE_ALERT, alert);
            log.error("Unable to create employer. Cause: {}", ex.getMessage());
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
