/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    OK("OK"),
    ERROR("ERROR"),
    ;

    private final String name;

    public static boolean isInvalid(BaseCommandResponse response) {
        return response.getCode().equals(ERROR.name);
    }
}
