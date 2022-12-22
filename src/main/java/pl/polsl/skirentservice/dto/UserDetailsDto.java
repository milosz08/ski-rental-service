/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserDetailsDto.java
 *  Last modified: 22.12.2022, 19:28
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentservice.dto;

import lombok.Data;
import pl.polsl.skirentservice.dao.UserEntity;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class UserDetailsDto {
    private String firstName;
    private String lastName;

    //------------------------------------------------------------------------------------------------------------------

    public UserDetailsDto(final UserEntity userEntity) {
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
    }
}
