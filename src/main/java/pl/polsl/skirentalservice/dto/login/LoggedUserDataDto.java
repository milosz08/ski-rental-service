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
import java.util.Objects;

import pl.polsl.skirentalservice.util.Gender;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class LoggedUserDataDto {
    private Long id;
    private String login;
    private String fullName;
    private String imageUrl;
    private String roleName;
    private Character roleAlias;
    private Gender gender;

    //------------------------------------------------------------------------------------------------------------------

    public LoggedUserDataDto(Long id, String login, String fullName, String imageUrl, String roleName,
                             Character roleAlias, Gender gender) {
        this.id = id;
        this.login = login;
        this.fullName = fullName;
        this.roleName = roleName;
        this.roleAlias = roleAlias;
        this.gender = gender;
        if (Objects.isNull(imageUrl)) {
            this.imageUrl = "static/images/default-profile-image.svg";
        } else {
            this.imageUrl = imageUrl;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", login='" + login + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleAlias=" + roleAlias +
                ", gender=" + gender +
                '}';
    }
}
