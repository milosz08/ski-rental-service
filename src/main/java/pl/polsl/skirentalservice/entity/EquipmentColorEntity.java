/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityInjector
@Table(name = "equipment_colors")
@NoArgsConstructor
public class EquipmentColorEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "equipmentColor", fetch = FetchType.LAZY)
    private Set<EquipmentEntity> equipments = new HashSet<>();

    public EquipmentColorEntity(String name) {
        this.name = name.toLowerCase();
    }

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

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            '}';
    }
}
