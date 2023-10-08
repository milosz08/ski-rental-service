/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao.user_details;

public interface IUserDetailsDao {
    boolean checkIfCustomerWithSamePeselExist(String pesel, Object customerId);
    boolean checkIfCustomerWithSameEmailExist(String email, Object customerId);
    boolean checkIfCustomerWithSamePhoneNumberExist(String phoneNumber, Object customerId);
    boolean checkIfEmployerWithSamePeselExist(String pesel, Object employerId);
    boolean checkIfEmployerWithSamePhoneNumberExist(String phoneNumber, Object employerId);
}
