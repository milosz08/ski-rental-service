/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserEntity.java
 *  Last modified: 22.12.2022, 20:37
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.EntityInjector;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "users")
@NoArgsConstructor
public class UserEntity extends AuditableEntity {

    @Column(name = "first_name")    private String firstName;
    @Column(name = "last_name")     private String lastName;

    //------------------------------------------------------------------------------------------------------------------

    public String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
