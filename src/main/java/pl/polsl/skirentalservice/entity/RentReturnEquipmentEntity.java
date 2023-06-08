/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RentReturnEquipmentEntity.java
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

import java.math.BigDecimal;

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "rent_returns_equipments")
@NoArgsConstructor
public class RentReturnEquipmentEntity extends AuditableEntity {

    @Column(name = "total_price")       private BigDecimal totalPrice;
    @Column(name = "description")       private String description;
    @Column(name = "deposit_price")     private BigDecimal depositPrice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "rent_return_id", referencedColumnName = "id")
    private RentReturnEntity rentReturn;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private EquipmentEntity equipment;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "rent_equipment_id", referencedColumnName = "id")
    private RentEquipmentEntity rentEquipment;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    BigDecimal getDepositPrice() {
        return depositPrice;
    }

    void setDepositPrice(BigDecimal depositPrice) {
        this.depositPrice = depositPrice;
    }

    RentReturnEntity getRentReturn() {
        return rentReturn;
    }

    public void setRentReturn(RentReturnEntity rentReturn) {
        this.rentReturn = rentReturn;
    }

    EquipmentEntity getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
    }

    RentEquipmentEntity getRentEquipment() {
        return rentEquipment;
    }

    public void setRentEquipment(RentEquipmentEntity rentEquipment) {
        this.rentEquipment = rentEquipment;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\

    @Override
    public String toString() {
        return "{" +
            "totalPrice=" + totalPrice +
            ", description='" + description +
            ", depositPrice=" + depositPrice +
            ", equipment=" + equipment +
            '}';
    }
}
