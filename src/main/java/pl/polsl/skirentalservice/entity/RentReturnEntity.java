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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Entity
@EntityInjector
@Table(name = "rent_returns")
@NoArgsConstructor
public class RentReturnEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String issuedIdentifier;

    private LocalDateTime issuedDateTime;

    private String description;

    private BigDecimal totalPrice;

    private BigDecimal totalDepositPrice;

    @JoinColumn
    @OneToOne(cascade = { PERSIST, MERGE })
    private RentEntity rent;

    @OneToMany(cascade = { PERSIST, MERGE }, mappedBy = "rentReturn")
    private Set<RentReturnEquipmentEntity> equipments = new HashSet<>();

    public String getIssuedIdentifier() {
        return issuedIdentifier;
    }

    public void setIssuedIdentifier(String issuedIdentifier) {
        this.issuedIdentifier = issuedIdentifier;
    }

    LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    BigDecimal getTotalDepositPrice() {
        return totalDepositPrice;
    }

    public void setTotalDepositPrice(BigDecimal totalDepositPrice) {
        this.totalDepositPrice = totalDepositPrice;
    }

    public RentEntity getRent() {
        return rent;
    }

    public void setRent(RentEntity rent) {
        this.rent = rent;
    }

    Set<RentReturnEquipmentEntity> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<RentReturnEquipmentEntity> equipments) {
        this.equipments = equipments;
    }

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime=" + issuedDateTime +
            ", description='" + description +
            ", totalPrice=" + totalPrice +
            ", totalDepositPrice=" + totalDepositPrice +
            '}';
    }
}
