/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import lombok.Data;

@Data
public class BaseCommandResponse {
    private String code;
    private String msg;
}
