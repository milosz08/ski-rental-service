package pl.polsl.skirentalservice.domain.owner;

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
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.service.OwnerEmployerService;
import pl.polsl.skirentalservice.util.AlertType;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;
import pl.polsl.skirentalservice.util.UserRole;

@Slf4j
@WebServlet("/owner/settings")
public class OwnerSettingsServlet extends AbstractWebServlet implements Attribute {
    private final OwnerEmployerService ownerEmployerService;
    private final ValidatorBean validatorBean;
    private final ServerConfigBean serverConfigBean;

    @Inject
    public OwnerSettingsServlet(
        OwnerEmployerService ownerEmployerService,
        ValidatorBean validatorBean,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.ownerEmployerService = ownerEmployerService;
        this.validatorBean = validatorBean;
        this.serverConfigBean = serverConfigBean;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.OWNER_SETTINGS_PAGE_ALERT);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();
        try {
            AddEditEmployerResDto resDto = req.getFromSession(this, AddEditEmployerResDto.class);
            if (resDto == null) {
                resDto = ownerEmployerService.getEmployerOrOwnerEditDetails(loggedUser.getId());
            }
            req.addAttribute("addEditOwnerData", resDto);
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.OWNER_SETTINGS_PAGE_ALERT, alert);
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("addEditText", "Edytuj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_SETTINGS_PAGE)
            .pageOrRedirectTo("owner/owner-settings")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("owner/settings", this, resDto);
        }
        final AlertTupleDto alert = new AlertTupleDto(true);
        String redirectUrl = "owner/settings";
        try {
            reqDto.validateDates(serverConfigBean);
            ownerEmployerService.editUserAccount(loggedUser.getId(), reqDto, UserRole.OWNER);

            alert.setType(AlertType.INFO);
            alert.setMessage("Twoje dane osobowe zostały pomyślnie zaktualizowane.");

            req.deleteSessionAttribute(this);
            req.setSessionAttribute(SessionAlert.COMMON_PROFILE_PAGE_ALERT, alert);
            redirectUrl = "owner/profile";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            req.setSessionAttribute(SessionAlert.OWNER_SETTINGS_PAGE_ALERT, alert);
            log.error("Unable to update owner personal data with id: {}. Cause: {}", loggedUser.getId(),
                ex.getMessage());
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
