/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IEmployerDao.java
 * Last modified: 3/12/23, 11:01 AM
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

package pl.polsl.skirentalservice.dao.employer;

import java.util.List;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.OwnerMailPayloadDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IEmployerDao {
    Optional<String> findEmployerPassword(String loginOrEmail);
    Optional<EmployerEntity> findEmployerBasedId(Object employerId);

    Optional<LoggedUserDataDto> findLoggedEmployerDetails(String loginOrEmail);
    Optional<EmployerDetailsDto> findEmployerDetails(String loginOrEmail);
    Optional<EmployerDetailsResDto> findEmployerPageDetails(Object employerId);
    Optional<AddEditEmployerReqDto> findEmployerEditPageDetails(Object employerId);

    void updateEmployerFirstAccessPassword(String newPassword, Object employerId);
    void updateEmployerPassword(String newPassword, Object employerId);

    boolean checkIfLoginAlreadyExist(String login);
    boolean checkIfEmployerHasOpenedRents(Object employerId);

    List<OwnerMailPayloadDto> findAllEmployersMailSenders();
    Long findAllEmployersCount(FilterDataDto filterData);
    List<EmployerRecordResDto> findAllPageableEmployersRecords(PageableDto pageableDto);
}
