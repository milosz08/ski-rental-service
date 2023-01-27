/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EmployerEntity.java
 *  Last modified: 29/12/2022, 20:43
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.entity;

import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDate;

import pl.polsl.skirentalservice.core.db.*;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Builder
@Table(name = "employeers")
@AllArgsConstructor
@NoArgsConstructor
public class EmployerEntity extends AuditableEntity {

    @Column(name = "login")                                 private String login;
    @Column(name = "password")                              private String password;
    @Column(name = "hired_date")                            private LocalDate hiredDate;
    @Column(name = "image_url")                             private String imageUrl;
    @Column(name = "first_access", insertable = false)      private Boolean firstAccess;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REMOVE })
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetailsEntity userDetails;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE, REMOVE })
    @JoinColumn(name = "location_address_id", referencedColumnName = "id")
    private LocationAddressEntity locationAddress;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmployerEntity(LocalDate hiredDate, UserDetailsEntity userDetails, LocationAddressEntity locationAddress) {
        this.hiredDate = hiredDate;
        this.userDetails = userDetails;
        this.locationAddress = locationAddress;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getLogin() {
        return login;
    }

    void setLogin(String login) {
        this.login = login;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    LocalDate getHiredDate() {
        return hiredDate;
    }

    public void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    RoleEntity getRole() {
        return role;
    }

    void setRole(RoleEntity role) {
        this.role = role;
    }

    Boolean getFirstAccess() {
        return firstAccess;
    }

    void setFirstAccess(Boolean firstAccess) {
        this.firstAccess = firstAccess;
    }

    public String getEmailAddress() {
        return userDetails.getEmailAddress();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "login='" + login +
            ", password='" + password +
            ", hiredDate=" + hiredDate +
            ", imageUrl='" + imageUrl +
            ", firstAccess=" + firstAccess +
            ", role=" + role +
            '}';
    }
}
