/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EmployerDetailsDto.java
 *  Last modified: 01/01/2023, 01:10
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class EmployerDetailsDto {
    private Long id;
    private String login;
    private String fullName;
    private String emailAddress;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "id=" + id +
            ", login='" + login + '\'' +
            ", fullName='" + fullName + '\'' +
            ", emailAddress='" + emailAddress + '\'' +
            '}';
    }
}
