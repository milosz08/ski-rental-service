/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EquipmentColorEntity.java
 * Last modified: 3/12/23, 11:01 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.entity;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "equipment_colors")
@NoArgsConstructor
public class EquipmentColorEntity extends AuditableEntity {

    @Column(name = "name")  private String name;

    @OneToMany(mappedBy = "equipmentColor", fetch = FetchType.LAZY)
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
