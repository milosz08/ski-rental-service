/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EquipmentColorEntity.java
 *  Last modified: 26/01/2023, 15:31
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import java.util.*;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.db.*;

import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "equipment_colors")
@NoArgsConstructor
public class EquipmentColorEntity extends AuditableEntity {

    @Column(name = "name")  private String name;

    @OneToMany(mappedBy = "equipmentColor", fetch = LAZY)
    private Set<EquipmentEntity> equipments = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EquipmentColorEntity(String name) {
        this.name = name.toLowerCase();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Set<EquipmentEntity> getEquipments() {
        return equipments;
    }

    void setEquipments(Set<EquipmentEntity> equipments) {
        this.equipments = equipments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "name='" + name +
            '}';
    }
}
