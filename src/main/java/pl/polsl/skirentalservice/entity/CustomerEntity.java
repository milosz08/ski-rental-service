/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CustomerEntity.java
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
