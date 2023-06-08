/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: RentEntity.java
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.converter.RentStatusConverter;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "employer_id", referencedColumnName = "id")
    private EmployerEntity employer;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "rent")
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
