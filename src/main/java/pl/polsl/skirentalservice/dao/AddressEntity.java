/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: AddressEntity.java
 *  Last modified: 28/12/2022, 00:09
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.*;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "user_addresses")
@NoArgsConstructor
public class AddressEntity extends AuditableEntity {

    @Column(name = "street")        private String street;
    @Column(name = "building_no")   private String buildingNo;
    @Column(name = "apartment_no")  private String apartmentNo;
    @Column(name = "city")          private String city;
    @Column(name = "postal_code")   private String postalCode;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, REMOVE })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

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

    UserEntity getUser() {
        return user;
    }

    void setUser(UserEntity user) {
        this.user = user;
    }
}
