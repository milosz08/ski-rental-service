package pl.polsl.skirentalservice.domain.common;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.core.ServerConfigBean;
import pl.polsl.skirentalservice.core.servlet.AbstractWebServlet;
import pl.polsl.skirentalservice.core.servlet.HttpMethodMode;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.WebServletResponse;
import pl.polsl.skirentalservice.dto.AlertTupleDto;
import pl.polsl.skirentalservice.dto.MultipleEquipmentsDataDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.RentDetailsResDto;
import pl.polsl.skirentalservice.service.RentService;
import pl.polsl.skirentalservice.util.PageTitle;
import pl.polsl.skirentalservice.util.SessionAlert;

import java.util.StringJoiner;

@Slf4j
@WebServlet(urlPatterns = {"/seller/rent-details", "/owner/rent-details"})
public class CommonRentDetailsServlet extends AbstractWebServlet {
    private final RentService rentService;

    @Inject
    public CommonRentDetailsServlet(
        RentService rentService,
        ServerConfigBean serverConfigBean
    ) {
        super(serverConfigBean);
        this.rentService = rentService;
    }

    @Override
    protected WebServletResponse httpGetCall(WebServletRequest req) {
        final Long rentId = req.getAttribute("rentId", Long.class);
        final LoggedUserDataDto loggedUser = req.getLoggedUser();

        final AlertTupleDto alert = new AlertTupleDto(true);

        String redirectOrViewName = loggedUser.getRoleEng() + "/rents";
        HttpMethodMode mode = HttpMethodMode.REDIRECT;
        try {
            final MultipleEquipmentsDataDto<RentDetailsResDto> rentDetails = rentService
                .getRentDetails(rentId, loggedUser);

            req.addAttribute("totalSum", rentDetails.totalCountOfRelatedElements());
            req.addAttribute("equipmentsRentDetailsData", rentDetails.relatedElements());
            req.addAttribute("rentDetailsData", rentDetails.details());

            mode = HttpMethodMode.JSP_GENERATOR;
            redirectOrViewName = new StringJoiner("/")
                .add(loggedUser.getRoleEng())
                .add("rent")
                .add(loggedUser.getRoleEng() + "-rent-details")
                .toString();
        } catch (AbstractAppException ex) {
            alert.setMessage(ex.getMessage());
            req.setSessionAttribute(SessionAlert.COMMON_RENTS_PAGE_ALERT, alert);
        }
        return WebServletResponse.builder()
            .mode(mode)
            .pageTitle(PageTitle.COMMON_RENT_DETAILS_PAGE)
            .pageOrRedirectTo(redirectOrViewName)
            .build();
    }
}
