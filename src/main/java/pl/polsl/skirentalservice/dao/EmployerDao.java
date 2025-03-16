package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dto.OwnerMailPayloadDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.change_password.EmployerDetailsDto;
import pl.polsl.skirentalservice.dto.employer.AddEditEmployerReqDto;
import pl.polsl.skirentalservice.dto.employer.EmployerDetailsResDto;
import pl.polsl.skirentalservice.dto.employer.EmployerRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.entity.EmployerEntity;

import java.util.List;
import java.util.Optional;

public interface EmployerDao {
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

    boolean checkIfEmployerExist(Object employerId);

    List<OwnerMailPayloadDto> findAllEmployersMailSenders();

    Long findAllEmployersCount(FilterDataDto filterData);

    List<EmployerRecordResDto> findAllPageableEmployersRecords(PageableDto pageableDto);
}
