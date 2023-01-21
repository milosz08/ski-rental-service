/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ReturnCode.java
 *  Last modified: 21/01/2023, 08:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.ssh;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum ReturnCode {
    OK("OK"),
    ERROR("ERROR");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isInvalid(BaseCommandResponse response) {
        return response.getCode().equals(ERROR.name);
    }
}
