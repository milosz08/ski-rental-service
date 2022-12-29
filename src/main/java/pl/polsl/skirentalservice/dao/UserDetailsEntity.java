/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: UserDetailsEntity.java
 *  Last modified: 29/12/2022, 20:44
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
import pl.polsl.skirentalservice.util.Gender;

import java.time.LocalDate;

//----------------------------------------------------------------------------------------------------------------------

@Entity
@EntityInjector
@Table(name = "user_details")
@NoArgsConstructor
public class UserDetailsEntity extends AuditableEntity {

    @Column(name = "first_name")        private String firstName;
    @Column(name = "last_name")         private String lastName;
    @Column(name = "pesel")             private String pesel;
    @Column(name = "phone_area_code")   private Integer phoneAreaCode;
    @Column(name = "phone_number")      private String phoneNumber;
    @Column(name = "email_address")     private String emailAddress;
    @Column(name = "born_date")         private LocalDate bornDate;
    @Column(name = "gender")            private Gender gender;

    //------------------------------------------------------------------------------------------------------------------

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

    LocalDate getBornDate() {
        return bornDate;
    }

    void setBornDate(LocalDate bornDate) {
        this.bornDate = bornDate;
    }

    Gender getGender() {
        return gender;
    }

    void setGender(Gender gender) {
        this.gender = gender;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pesel='" + pesel + '\'' +
                ", phoneAreaCode=" + phoneAreaCode +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", bornDate=" + bornDate +
                ", gender=" + gender +
                '}';
    }
}
