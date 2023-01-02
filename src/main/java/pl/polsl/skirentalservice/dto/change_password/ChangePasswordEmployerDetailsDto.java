/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ChangePasswordEmployerDetailsDto.java
 *  Last modified: 01/01/2023, 20:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.change_password;

import lombok.*;
import java.util.Objects;

//----------------------------------------------------------------------------------------------------------------------

@Data
public class ChangePasswordEmployerDetailsDto {
    private Long id;
    private Long tokenId;
    private String fullName;
    private String imageUrl;

    //------------------------------------------------------------------------------------------------------------------

    public ChangePasswordEmployerDetailsDto(Long id, Long tokenId, String fullName, String imageUrl) {
        this.id = id;
        this.tokenId = tokenId;
        this.fullName = fullName;
        if (Objects.isNull(imageUrl)) {
            this.imageUrl = "static/images/default-profile-image.svg";
        } else {
            this.imageUrl = imageUrl;
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public ChangePasswordEmployerDetailsDto() {
        this.imageUrl = "static/images/default-profile-image.svg";
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
