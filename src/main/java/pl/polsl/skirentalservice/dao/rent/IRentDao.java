/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IRentDao.java
 * Last modified: 6/3/23, 1:26 AM
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

package pl.polsl.skirentalservice.dao.rent;

import java.util.List;
import java.util.Optional;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.rent.OwnerRentRecordResDto;
import pl.polsl.skirentalservice.dto.rent.RentDetailsResDto;
import pl.polsl.skirentalservice.dto.rent.SellerRentRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.RentReturnDetailsResDto;
import pl.polsl.skirentalservice.entity.RentEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IRentDao {
    Optional<RentReturnDetailsResDto> findRentReturnDetails(Object rentId, Object employerId);
    Optional<RentDetailsResDto> findRentDetails(Object rentId, Object employerId, String roleAlias);

    boolean checkIfRentIsFromEmployer(Object rentId, Object employerId);
    boolean checkIfIssuerExist(Object issuer);
    void updateRentStatus(RentStatus rentStatus, Object rentId);

    Long findAllRentsCount(FilterDataDto filterData);
    Long findAllRentsFromEmployerCount(FilterDataDto filterData, Long employerId);

    List<RentEntity> findAllRentsBaseCustomerId(Object customerId);
    List<OwnerRentRecordResDto> findAllPageableRents(PageableDto pageableDto);
    List<SellerRentRecordResDto> findAllPageableRentsFromEmployer(PageableDto pageableDto, Object employerId);
}
