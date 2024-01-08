/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import static jakarta.persistence.CascadeType.*;

@Entity
@EntityInjector
@Table(name = "rent_equipments")
@NoArgsConstructor
public class RentEquipmentEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private Integer count;

    private BigDecimal totalPrice;

    private String description;

    private BigDecimal depositPrice;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE, REMOVE })
    private RentEntity rent;

    @JoinColumn
    @ManyToOne(cascade = { PERSIST, MERGE })
    private EquipmentEntity equipment;

    public Integer getCount() {
        return count;
    }

    void setCount(Integer count) {
        this.count = count;
    }

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

    public void setDepositPrice(BigDecimal depositPrice) {
        this.depositPrice = depositPrice;
    }

    RentEntity getRent() {
        return rent;
    }

    public void setRent(RentEntity rent) {
        this.rent = rent;
    }

    public EquipmentEntity getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return "{" +
            "count=" + count +
            ", totalPrice=" + totalPrice +
            ", description=" + description +
            ", depositPrice=" + depositPrice +
            ", equipment=" + equipment +
            '}';
    }
}
