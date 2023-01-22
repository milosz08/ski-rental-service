/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CustomerEntity.java
 *  Last modified: 29/12/2022, 20:43
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

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "customers")
@NoArgsConstructor
public class CustomerEntity extends AuditableEntity {

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetailsEntity userDetails;

    @ManyToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinTable(name = "customers_addresses_binding",
        joinColumns = { @JoinColumn(name = "customer_id") },
        inverseJoinColumns = { @JoinColumn(name = "location_address_id") })
    private LocationAddressEntity locationAddress;

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "userDetails=" + userDetails +
            ", address=" + locationAddress +
            '}';
    }
}
