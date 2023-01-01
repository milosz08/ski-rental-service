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

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.db.*;

import java.time.LocalDate;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "employeers")
@NoArgsConstructor
public class EmployerEntity extends AuditableEntity {

    @Column(name = "login")             private String login;
    @Column(name = "password")          private String password;
    @Column(name = "hired_date")        private LocalDate hiredDate;
    @Column(name = "image_url")         private String imageUrl;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "user_details_id", referencedColumnName = "id")
    private UserDetailsEntity userDetails;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "location_address_id", referencedColumnName = "id")
    private LocationAddressEntity locationAddress;

    @OneToOne(fetch = LAZY, cascade = { PERSIST, MERGE })
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

    //------------------------------------------------------------------------------------------------------------------

    public String getLogin() {
        return login;
    }

    void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    LocalDate getHiredDate() {
        return hiredDate;
    }

    void setHiredDate(LocalDate hiredDate) {
        this.hiredDate = hiredDate;
    }

    public String getImageUrl() {
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

    LocationAddressEntity getLocationAddress() {
        return locationAddress;
    }

    void setLocationAddress(LocationAddressEntity locationAddress) {
        this.locationAddress = locationAddress;
    }

    public RoleEntity getRole() {
        return role;
    }

    void setRole(RoleEntity role) {
        this.role = role;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return '{' +
                "login='" + login + '\'' +
                ", hiredDate=" + hiredDate +
                ", imageUrl='" + imageUrl + '\'' +
                ", userDetails=" + userDetails +
                ", locationAddress=" + locationAddress +
                ", role=" + role +
                '}';
    }
}
