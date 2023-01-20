/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LoginFetchDto.java
 *  Last modified: 29/12/2022, 22:10
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
    private String imageUrl;
    private String roleName;
    private Character roleAlias;
    private String roleEng;
    private Gender gender;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "id=" + id +
            ", login='" + login + '\'' +
            ", fullName='" + fullName + '\'' +
            ", imageUrl='" + imageUrl + '\'' +
            ", roleName='" + roleName + '\'' +
            ", roleAlias=" + roleAlias +
            ", roleEng='" + roleEng + '\'' +
            ", gender=" + gender +
            '}';
    }
}
