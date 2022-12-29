/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RoleEntity.java
 *  Last modified: 28/12/2022, 01:15
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.*;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity extends AuditableEntity {

    @Column(name = "role_name")     private String roleName;
    @Column(name = "alias")         private Character alias;

    //------------------------------------------------------------------------------------------------------------------

    public String getRoleName() {
        return roleName;
    }

    void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Character getAlias() {
        return alias;
    }

    void setAlias(Character alias) {
        this.alias = alias;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{" +
                "roleName='" + roleName + '\'' +
                ", alias=" + alias +
                '}';
    }
}
