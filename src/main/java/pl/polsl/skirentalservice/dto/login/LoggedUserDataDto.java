/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: LoggedUserDataDto.java
 *  Last modified: 30/01/2023, 20:13
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.login;

import lombok.*;
import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class LoggedUserDataDto {
    private Long id;
    private String login;
    private String fullName;
    private String roleName;
    private Character roleAlias;
    private String roleEng;
    private Gender gender;
    private String emailAddress;
    private Boolean isFirstAccess;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", login='" + login +
            ", fullName='" + fullName +
            ", roleName='" + roleName +
            ", roleAlias=" + roleAlias +
            ", roleEng='" + roleEng +
            ", gender=" + gender +
            ", emailAddress='" + emailAddress +
            ", isFirstAccess='" + isFirstAccess +
            '}';
    }
}
