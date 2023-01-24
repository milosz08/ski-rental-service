/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EquipmentTypeEntity.java
 *  Last modified: 24/01/2023, 14:46
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.db.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "equipment_types")
@NoArgsConstructor
public class EquipmentTypeEntity extends AuditableEntity {

    @Column(name = "name")  private String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EquipmentTypeEntity(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name + '\'' +
            '}';
    }
}
