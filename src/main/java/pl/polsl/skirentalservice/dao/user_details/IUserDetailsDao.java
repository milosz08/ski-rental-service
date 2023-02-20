/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: IUserDetailsDao.java
 *  Last modified: 20/02/2023, 19:24
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.user_details;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IUserDetailsDao {
    boolean checkIfCustomerWithSamePeselExist(String pesel, Object customerId);
    boolean checkIfCustomerWithSameEmailExist(String email, Object customerId);
    boolean checkIfCustomerWithSamePhoneNumberExist(String phoneNumber, Object customerId);
    boolean checkIfEmployerWithSamePeselExist(String pesel, Object employerId);
    boolean checkIfEmployerWithSamePhoneNumberExist(String phoneNumber, Object employerId);
}
