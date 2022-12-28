/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: BannerTupleDto.java
 *  Last modified: 25.12.2022, 03:13
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

//----------------------------------------------------------------------------------------------------------------------

@Data
@AllArgsConstructor
public class AlertTupleDto {
    private boolean active;
    private String message;
    private AlertType type;

    public AlertTupleDto() {
        this.active = false;
        this.message = "";
        this.type = ERROR;
    }
}
