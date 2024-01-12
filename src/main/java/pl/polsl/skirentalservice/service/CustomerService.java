/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

@Local
public interface CustomerService {
    Slice<CustomerRecordResDto> getPageableCustomers(PageableDto pageableDto, String addressColumn);
    AddEditCustomerReqDto getCustomerDetails(Long customerId);
    CustomerDetailsResDto getCustomerFullDetails(Long customerId);
    void createNewCustomer(AddEditCustomerReqDto reqDto, LoggedUserDataDto loggedUser);
    void editCustomerDetails(AddEditCustomerReqDto reqDto, Long customerId);
    void deleteCustomer(Long customerId, WebServletRequest req);
    boolean checkIfCustomerExist(Long customerId);
}
