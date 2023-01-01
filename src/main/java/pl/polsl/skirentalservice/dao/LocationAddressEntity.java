/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: LocationAddressEntity.java
 *  Last modified: 29/12/2022, 20:44
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.db.*;

import java.util.Set;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "location_addresses")
@NoArgsConstructor
public class LocationAddressEntity extends AuditableEntity {

    @Column(name = "street")        private String street;
    @Column(name = "building_no")   private String buildingNo;
    @Column(name = "apartment_no")  private String apartmentNo;
    @Column(name = "city")          private String city;
    @Column(name = "postal_code")   private String postalCode;

    @OneToMany(fetch = LAZY, cascade = { PERSIST, MERGE }, mappedBy = "locationAddress")
    private Set<CustomerEntity> customers;

    //------------------------------------------------------------------------------------------------------------------

    String getStreet() {
        return street;
    }

    void setStreet(String street) {
        this.street = street;
    }

    String getBuildingNo() {
        return buildingNo;
    }

    void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    String getApartmentNo() {
        return apartmentNo;
    }

    void setApartmentNo(String apartmentNo) {
        this.apartmentNo = apartmentNo;
    }

    String getCity() {
        return city;
    }

    void setCity(String city) {
        this.city = city;
    }

    String getPostalCode() {
        return postalCode;
    }

    void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    Set<CustomerEntity> getCustomers() {
        return customers;
    }

    void setCustomers(Set<CustomerEntity> customers) {
        this.customers = customers;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "street='" + street + '\'' +
                ", buildingNo='" + buildingNo + '\'' +
                ", apartmentNo='" + apartmentNo + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
