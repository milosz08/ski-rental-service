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
import java.math.BigDecimal;

import static jakarta.persistence.CascadeType.*;

@Entity
@EntityInjector
@Table(name = "rent_returns_equipments")
@NoArgsConstructor
public class RentReturnEquipmentEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private BigDecimal totalPrice;

    private String description;

    private BigDecimal depositPrice;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE, REMOVE })
    private RentReturnEntity rentReturn;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE })
    private EquipmentEntity equipment;

    @JoinColumn
    @OneToOne(cascade = { PERSIST, MERGE })
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
