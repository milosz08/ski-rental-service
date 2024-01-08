/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.converter.GenderConverter;
import pl.polsl.skirentalservice.core.db.AuditableEntity;
import pl.polsl.skirentalservice.core.db.EntityInjector;
import pl.polsl.skirentalservice.util.Gender;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@EntityInjector
@Table(name = "user_details")
@NoArgsConstructor
public class UserDetailsEntity extends AuditableEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private String firstName;

    private String lastName;

    private String pesel;

    private String phoneNumber;

    private String emailAddress;

    private LocalDate bornDate;

    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(insertable = false, updatable = false)
    private Integer phoneAreaCode;

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

    @Override
    public String toString() {
        return '{' +
            "firstName=" + firstName +
            ", lastName=" + lastName +
            ", pesel=" + pesel +
            ", phoneAreaCode=" + phoneAreaCode +
            ", phoneNumber=" + phoneNumber +
            ", emailAddress=" + emailAddress +
            ", bornDate=" + bornDate +
            ", gender=" + gender +
            '}';
    }
}
