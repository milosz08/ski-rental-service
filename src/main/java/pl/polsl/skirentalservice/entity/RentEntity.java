/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: RentEquipmentEntity.java
 *  Last modified: 29/01/2023, 15:00
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.core.db.*;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.converter.RentStatusConverter;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "rents")
@NoArgsConstructor
public class RentEntity extends AuditableEntity {

    @Column(name = "issued_identifier")             private String issuedIdentifier;
    @Column(name = "issued_datetime")               private LocalDateTime issuedDateTime;
    @Column(name = "rent_datetime")                 private LocalDateTime rentDateTime;
    @Column(name = "return_datetime")               private LocalDateTime returnDateTime;
    @Column(name = "tax")                           private Integer tax;
    @Column(name = "description")                   private String description;
    @Column(name = "total_price")                   private BigDecimal totalPrice;
    @Column(name = "total_deposit_price")           private BigDecimal totalDepositPrice;

    @Column(name = "status") @Convert(converter = RentStatusConverter.class)
    private RentStatus status;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private EmployerEntity employer;

    @OneToMany(fetch = LAZY, cascade = { PERSIST, MERGE, REMOVE }, mappedBy = "rent")
    private Set<RentEquipmentEntity> equipments = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getIssuedIdentifier() {
        return issuedIdentifier;
    }

    void setIssuedIdentifier(String issuedIdentifier) {
        this.issuedIdentifier = issuedIdentifier;
    }

    LocalDateTime getIssuedDateTime() {
        return issuedDateTime;
    }

    public void setIssuedDateTime(LocalDateTime issuedDateTime) {
        this.issuedDateTime = issuedDateTime;
    }

    LocalDateTime getRentDateTime() {
        return rentDateTime;
    }

    void setRentDateTime(LocalDateTime rentDateTime) {
        this.rentDateTime = rentDateTime;
    }

    LocalDateTime getReturnDateTime() {
        return returnDateTime;
    }

    void setReturnDateTime(LocalDateTime returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    public RentStatus getStatus() {
        return status;
    }

    public void setStatus(RentStatus status) {
        this.status = status;
    }

    Integer getTax() {
        return tax;
    }

    void setTax(Integer tax) {
        this.tax = tax;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
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

    CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    EmployerEntity getEmployer() {
        return employer;
    }

    public void setEmployer(EmployerEntity employer) {
        this.employer = employer;
    }

    public Set<RentEquipmentEntity> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<RentEquipmentEntity> equipments) {
        this.equipments = equipments;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime=" + issuedDateTime +
            ", rentDateTime=" + rentDateTime +
            ", returnDateTime=" + returnDateTime +
            ", status=" + status +
            ", tax=" + tax +
            ", description='" + description +
            ", totalPrice=" + totalPrice +
            ", totalDepositPrice=" + totalDepositPrice +
            ", customer=" + customer +
            ", employer=" + employer +
            '}';
    }
}
