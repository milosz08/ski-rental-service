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
import pl.polsl.skirentalservice.util.UserRole;

@Slf4j
@WebServlet("/owner/edit-employer")
public class OwnerEditEmployerServlet extends AbstractWebServlet implements Attribute {
    private final ValidatorBean validatorBean;
    private final ServerConfigBean serverConfigBean;
    private final OwnerEmployerService ownerEmployerService;

    @Inject
    public OwnerEditEmployerServlet(
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
        final Long employerId = req.getAttribute("employerId", Long.class);
        final AlertTupleDto alert = req.getAlertAndDestroy(SessionAlert.OWNER_EDIT_EMPLOYER_PAGE_ALERT);
        try {
            AddEditEmployerResDto resDto = req.getFromSessionAndDestroy(this, AddEditEmployerResDto.class);
            if (resDto == null) {
                resDto = ownerEmployerService.getEmployerOrOwnerEditDetails(employerId);
            }
            req.addAttribute("addEditEmployerData", resDto);
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.OWNER_EDIT_EMPLOYER_PAGE_ALERT, alert);
        }
        req.addAttribute("alertData", alert);
        req.addAttribute("addEditText", "Edytuj");

        return WebServletResponse.builder()
            .mode(HttpMethodMode.JSP_GENERATOR)
            .pageTitle(PageTitle.OWNER_EDIT_EMPLOYER_PAGE)
            .pageOrRedirectTo("owner/employer/owner-add-edit-employer")
            .build();
    }

    @Override
    protected WebServletResponse httpPostCall(WebServletRequest req) {
        final Long employerId = req.getAttribute("employerId", Long.class);
        final AlertTupleDto alert = new AlertTupleDto(true);

        final AddEditEmployerReqDto reqDto = new AddEditEmployerReqDto(req);
        final AddEditEmployerResDto resDto = new AddEditEmployerResDto(validatorBean, reqDto);
        if (validatorBean.someFieldsAreInvalid(reqDto)) {
            throw new WebServletRedirectException("owner/edit-employer?id=" + employerId, this, resDto);
        }
        String redirectUrl = "owner/edit-employer?id=" + employerId;
        try {
            reqDto.validateDates(serverConfigBean);
            ownerEmployerService.editUserAccount(employerId, reqDto, UserRole.SELLER);

            alert.setType(AlertType.INFO);
            alert.setMessage(
                "Dane pracownika z ID <strong>#" + employerId + "</strong> zostały pomyślnie zaktualizowane."
            );
            req.deleteSessionAttribute(this);
            redirectUrl = "owner/employers";
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(this, resDto);
            log.error("Unable to update existing employer with id: {}. Cause: {}", employerId, ex.getMessage());
        }
        req.setSessionAttribute(SessionAlert.OWNER_EMPLOYERS_PAGE_ALERT, alert);
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
