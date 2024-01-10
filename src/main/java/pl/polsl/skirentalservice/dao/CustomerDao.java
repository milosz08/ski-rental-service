/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    boolean checkIfCustomerExist(Object rentId);
    boolean checkIfCustomerHasAnyActiveRents(Object customerId);
    Optional<CustomerDetailsResDto> findCustomerDetails(Object customerId);
    Optional<CustomerDetailsReturnResDto> findCustomerDetailsForReturnDocument(Object rentId);
    Optional<AddEditCustomerReqDto> findCustomerEditPageDetails(Object customerId);
    Long findAllCustomersCount(FilterDataDto filterData);
    List<CustomerRecordResDto> findAllPageableCustomers(PageableDto pageableDto, String addressColumn);
}
