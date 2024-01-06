/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityInjector
@Table(name = "location_addresses")
@NoArgsConstructor
public class LocationAddressEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @Column(name = "street")
    private String street;

    @Column(name = "building_no")
    private String buildingNr;

    @Column(name = "apartment_no")
    private String apartmentNr;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

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

    @Override
    public String toString() {
        return '{' +
            "street=" + street +
            ", buildingNo=" + buildingNr +
            ", apartmentNo=" + apartmentNr +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}
