/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: LocationAddressEntity.java
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

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

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
            "street='" + street +
            ", buildingNo='" + buildingNr +
            ", apartmentNo='" + apartmentNr +
            ", city='" + city +
            ", postalCode='" + postalCode +
            '}';
    }
}
