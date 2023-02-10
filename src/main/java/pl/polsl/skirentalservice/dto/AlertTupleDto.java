/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: AlertTupleDto.java
 *  Last modified: 20/01/2023, 05:16
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import lombok.*;
import pl.polsl.skirentalservice.util.AlertType;

import static pl.polsl.skirentalservice.util.AlertType.ERROR;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class AlertTupleDto {
    private boolean active;
    private String message;
    private AlertType type;
    private boolean disableContent;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public AlertTupleDto(boolean active) {
        this();
        this.active = active;
    }

    public AlertTupleDto() {
        this.type = ERROR;
    }
}
