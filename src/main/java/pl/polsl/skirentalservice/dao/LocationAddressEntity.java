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

import lombok.*;
import jakarta.persistence.*;

import pl.polsl.skirentalservice.core.db.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "location_addresses")
@NoArgsConstructor
public class LocationAddressEntity extends AuditableEntity {

    @Column(name = "street")        private String street;
    @Column(name = "building_no")   private String buildingNr;
    @Column(name = "apartment_no")  private String apartmentNr;
    @Column(name = "city")          private String city;
    @Column(name = "postal_code")   private String postalCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    String getBuildingNr() {
        return buildingNr;
    }

    public void setBuildingNr(String buildingNr) {
        this.buildingNr = buildingNr;
    }

    String getApartmentNr() {
        return apartmentNr;
    }

    public void setApartmentNr(String apartmentNr) {
        this.apartmentNr = apartmentNr;
    }

    String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "street='" + street + '\'' +
            ", buildingNo='" + buildingNr + '\'' +
            ", apartmentNo='" + apartmentNr + '\'' +
            ", city='" + city + '\'' +
            ", postalCode='" + postalCode + '\'' +
            '}';
    }
}
