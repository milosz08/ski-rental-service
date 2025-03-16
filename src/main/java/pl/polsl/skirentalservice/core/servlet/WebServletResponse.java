package pl.polsl.skirentalservice.core.servlet;

import lombok.Builder;
import pl.polsl.skirentalservice.util.PageTitle;

@Builder
public record WebServletResponse(
    PageTitle pageTitle,
    String pageOrRedirectTo,
    HttpMethodMode mode
) {
}
