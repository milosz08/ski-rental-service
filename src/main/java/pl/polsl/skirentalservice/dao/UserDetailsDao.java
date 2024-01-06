/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao;

public interface UserDetailsDao {
    boolean checkIfCustomerWithSamePeselExist(String pesel, Object customerId);
    boolean checkIfCustomerWithSameEmailExist(String email, Object customerId);
    boolean checkIfCustomerWithSamePhoneNumberExist(String phoneNumber, Object customerId);
    boolean checkIfEmployerWithSamePeselExist(String pesel, Object employerId);
    boolean checkIfEmployerWithSamePhoneNumberExist(String phoneNumber, Object employerId);
}
