/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.util.UserRole;

@Local
public interface OwnerEmployerService {
    Slice<EmployerRecordResDto> getPageableEmployers(PageableDto pageableDto);
    AddEditEmployerResDto getEmployerOrOwnerEditDetails(Long employerId);
    EmployerDetailsResDto getEmployerFullDetails(Long employerId);
    String addEmplyerByOwner(AddEditEmployerReqDto reqDto, LoggedUserDataDto loggedUserDataDto, WebServletRequest req);
    void editUserAccount(Long employerId, AddEditEmployerReqDto reqDto, UserRole role);
    void deleteEmployer(Long employerId);
    boolean checkIfEmployerExist(Long employerId);
}
