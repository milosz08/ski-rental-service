/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
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
