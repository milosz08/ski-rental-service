/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserEntity.java
 *  Last modified: 22.12.2022, 20:37
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import pl.polsl.skirentalservice.core.*;

import java.util.Set;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.FetchType.LAZY;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "users")
@NoArgsConstructor
public class UserEntity extends AuditableEntity {

    @Column(name = "login")             private String login;
    @Column(name = "password")          private String password;
    @Column(name = "first_name")        private String firstName;
    @Column(name = "last_name")         private String lastName;
    @Column(name = "pesel")             private String pesel;
    @Column(name = "phone_area_code")   private Integer phoneAreaCode;
    @Column(name = "phone_number")      private String phoneNumber;
    @Column(name = "email_address")     private String emailAddress;
    @Column(name = "image_url")         private String imageUrl;

    @OneToMany(fetch = LAZY, cascade = { PERSIST, REMOVE }, mappedBy = "user")
    private Set<AddressEntity> addresses;

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

    void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getPesel() {
        return pesel;
    }

    void setPesel(String pesel) {
        this.pesel = pesel;
    }

    Integer getPhoneAreaCode() {
        return phoneAreaCode;
    }

    void setPhoneAreaCode(Integer phoneAreaCode) {
        this.phoneAreaCode = phoneAreaCode;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    String getEmailAddress() {
        return emailAddress;
    }

    void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    Set<AddressEntity> getAddresses() {
        return addresses;
    }

    void setAddresses(Set<AddressEntity> address) {
        this.addresses = address;
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
        return "{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pesel='" + pesel + '\'' +
                ", phoneAreaCode=" + phoneAreaCode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
