/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentReturnEntity.java
 *  Last modified: 07/02/2023, 17:13
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Set;
import java.util.HashSet;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "rent_returns")
@NoArgsConstructor
public class RentReturnEntity extends AuditableEntity {

    @Column(name = "issued_identifier")             private String issuedIdentifier;
    @Column(name = "issued_datetime")               private LocalDateTime issuedDateTime;
    @Column(name = "description")                   private String description;
    @Column(name = "total_price")                   private BigDecimal totalPrice;
    @Column(name = "total_deposit_price")           private BigDecimal totalDepositPrice;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "rent_id", referencedColumnName = "id")
    private RentEntity rent;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "rentReturn")
    private Set<RentReturnEquipmentEntity> equipments = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
