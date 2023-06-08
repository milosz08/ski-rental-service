/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: UserDetailsEntity.java
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

import java.time.LocalDate;

import pl.polsl.skirentalservice.util.Gender;
import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.converter.GenderConverter;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Entity
@EntityInjector
@Table(name = "user_details")
@NoArgsConstructor
public class UserDetailsEntity extends AuditableEntity {

    @Column(name = "first_name")                    private String firstName;
    @Column(name = "last_name")                     private String lastName;
    @Column(name = "pesel")                         private String pesel;
    @Column(name = "phone_number")                  private String phoneNumber;
    @Column(name = "email_address")                 private String emailAddress;
    @Column(name = "born_date")                     private LocalDate bornDate;

    @Convert(converter = GenderConverter.class) @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_area_code", insertable = false, updatable = false)
    private Integer phoneAreaCode;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    LocalDate getBornDate() {
        return bornDate;
    }

    public void setBornDate(LocalDate bornDate) {
        this.bornDate = bornDate;
    }

    Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return '{' +
            "firstName='" + firstName +
            ", lastName='" + lastName +
            ", pesel='" + pesel +
            ", phoneAreaCode=" + phoneAreaCode +
            ", phoneNumber='" + phoneNumber +
            ", emailAddress='" + emailAddress +
            ", bornDate=" + bornDate +
            ", gender=" + gender +
            '}';
    }
}
