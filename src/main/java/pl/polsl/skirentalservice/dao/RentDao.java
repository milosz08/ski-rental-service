/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto;
import pl.polsl.skirentalservice.entity.RentEntity;
import pl.polsl.skirentalservice.util.RentStatus;

import java.util.List;
import java.util.Optional;

public interface RentDao {
    Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId);
    Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias);
    boolean checkIfRentExist(Object rentId);
    boolean checkIfRentIsFromEmployer(Object rentId, Object employerId);
    boolean checkIfIssuerExist(String issuer);
    void updateRentStatus(RentStatus rentStatus, Object rentId);
    Long findAllRentsCount(FilterDataDto filterData);
    Long findAllRentsFromEmployerCount(FilterDataDto filterData, Object employerId);
    List<RentEntity> findAllRentsBaseCustomerId(Object customerId);
    List<OwnerRentRecordResDto> findAllPageableRents(PageableDto pageableDto);
    List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(PageableDto pageableDto, Object employerId);
}
