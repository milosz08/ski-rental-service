/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.math.BigDecimal;

@Entity
@EntityInjector
@Table(name = "rent_returns_equipments")
@NoArgsConstructor
public class RentReturnEquipmentEntity extends AuditableEntity {

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "deposit_price")
    private BigDecimal depositPrice;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "rent_return_id", referencedColumnName = "id")
    private RentReturnEntity rentReturn;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    private EquipmentEntity equipment;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "rent_equipment_id", referencedColumnName = "id")
    private RentEquipmentEntity rentEquipment;

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

    @Override
    public String toString() {
        return "{" +
            "totalPrice=" + totalPrice +
            ", description=" + description +
            ", depositPrice=" + depositPrice +
            ", equipment=" + equipment +
            '}';
    }
}
