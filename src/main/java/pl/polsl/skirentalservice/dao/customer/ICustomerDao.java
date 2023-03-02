/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: CustomerDao.java
 *  Last modified: 20/02/2023, 18:52
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.customer;

import java.util.List;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.customer.CustomerRecordResDto;
import pl.polsl.skirentalservice.dto.customer.AddEditCustomerReqDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.CustomerDetailsReturnResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface ICustomerDao {
    boolean checkIfCustomerExist(Object rentId);
    boolean checkIfCustomerHasAnyActiveRents(Object customerId);

    Optional<CustomerDetailsResDto> findCustomerDetails(Object customerId);
    Optional<CustomerDetailsReturnResDto> findCustomerDetailsForReturnDocument(Object rentId);
    Optional<AddEditCustomerReqDto> findCustomerEditPageDetails(Object customerId);

    Long findAllCustomersCount(FilterDataDto filterData);
    List<CustomerRecordResDto> findAllPageableCustomers(PageableDto pageableDto, String addressColumn);
}
