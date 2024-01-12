/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.polsl.skirentalservice.core.servlet.session.Attribute;

@Getter
@AllArgsConstructor
public class WebServletRedirectException extends RuntimeException {
    private final String redirectTo;
    private Attribute attribute;
    private Object data;

    public WebServletRedirectException(String redirectTo) {
        this.redirectTo = redirectTo;
    }
}
