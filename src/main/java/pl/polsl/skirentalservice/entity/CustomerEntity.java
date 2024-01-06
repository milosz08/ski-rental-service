/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityInjector
@Table(name = "customers")
@NoArgsConstructor
public class CustomerEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetailsEntity userDetails;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
    @JoinColumn(name = "location_address_id", referencedColumnName = "id")
    private LocationAddressEntity locationAddress;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "customer")
    private Set<RentEntity> rents = new HashSet<>();

    public CustomerEntity(UserDetailsEntity userDetails, LocationAddressEntity locationAddress) {
        this.userDetails = userDetails;
        this.locationAddress = locationAddress;
    }

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

    @Override
    public String toString() {
        return '{' +
            "userDetails=" + userDetails +
            ", address=" + locationAddress +
            '}';
    }
}
