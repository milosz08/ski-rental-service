/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: EmployerDao.java
 *  Last modified: 20/02/2023, 18:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.employer;

import java.util.*;

import pl.polsl.skirentalservice.dto.*;
import pl.polsl.skirentalservice.dto.employer.*;
import pl.polsl.skirentalservice.entity.EmployerEntity;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto;

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
