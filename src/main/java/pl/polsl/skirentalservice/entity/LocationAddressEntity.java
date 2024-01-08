/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

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

    private String street;

    private String buildingNo;

    private String apartmentNo;

    private String city;

    private String postalCode;

    String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    String getApartmentNo() {
        return apartmentNo;
    }

    public void setApartmentNo(String apartmentNo) {
        this.apartmentNo = apartmentNo;
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
            ", buildingNo=" + buildingNo +
            ", apartmentNo=" + apartmentNo +
            ", city=" + city +
            ", postalCode=" + postalCode +
            '}';
    }
}
