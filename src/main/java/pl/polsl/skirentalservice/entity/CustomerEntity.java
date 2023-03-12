/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CustomerEntity.java
 *  Last modified: 29/01/2023, 15:17
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

import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "customers")
@NoArgsConstructor
public class CustomerEntity extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetailsEntity userDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "location_address_id", referencedColumnName = "id")
    private LocationAddressEntity locationAddress;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "customer")
    private Set<RentEntity> rents = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CustomerEntity(UserDetailsEntity userDetails, LocationAddressEntity locationAddress) {
        this.userDetails = userDetails;
        this.locationAddress = locationAddress;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public UserDetailsEntity getUserDetails() {
        return userDetails;
    }

    void setUserDetails(UserDetailsEntity userDetails) {
        this.userDetails = userDetails;
    }

    public LocationAddressEntity getLocationAddress() {
        return locationAddress;
    }

    void setLocationAddress(LocationAddressEntity locationAddress) {
        this.locationAddress = locationAddress;
    }

    Set<RentEntity> getRents() {
        return rents;
    }

    void setRents(Set<RentEntity> rents) {
        this.rents = rents;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "userDetails=" + userDetails +
            ", address=" + locationAddress +
            '}';
    }
}
