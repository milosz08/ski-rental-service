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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityInjector
@Table(name = "rent_returns")
@NoArgsConstructor
public class RentReturnEntity extends AuditableEntity {

    @Column(name = "issued_identifier")
    private String issuedIdentifier;

    @Column(name = "issued_datetime")
    private LocalDateTime issuedDateTime;

    @Column(name = "description")
    private String description;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "total_deposit_price")
    private BigDecimal totalDepositPrice;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "rent_id", referencedColumnName = "id")
    private RentEntity rent;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "rentReturn")
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
