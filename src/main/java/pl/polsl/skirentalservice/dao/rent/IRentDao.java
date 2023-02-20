/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: RentDao.java
 *  Last modified: 20/02/2023, 18:57
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.rent;

import java.util.*;

import pl.polsl.skirentalservice.dto.rent.*;
import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IRentDao {
    Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId);
    Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias);

    void updateRentStatus(RentStatus rentStatus, Object rentId);

    Long findAllRentsCount(FilterDataDto filterData);
    Long findAllRentsFromEmployerCount(FilterDataDto filterData, Long employerId);

    List<RentEntity> findAllRentsBaseCustomerId(Object customerId);
    List<OwnerRentRecordResDto> findAllPageableRents(
        FilterDataDto filterData, SorterDataDto sorterData, int page, int total);
    List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(
        FilterDataDto filterData, SorterDataDto sorterData, Long employerId, int page, int total);
}
