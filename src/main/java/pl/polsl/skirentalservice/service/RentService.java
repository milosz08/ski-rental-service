/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dto.MultipleEquipmentsDataDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.UpdatedInMemoryRentData;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;
import pl.polsl.skirentalservice.dto.rent.*;

import java.util.List;

@Local
public interface RentService {
    Slice<OwnerRentRecordResDto> getPageableOwnerRents(PageableDto pageableDto);
    Slice<SellerRentRecordResDto> getPageableEmployerRents(PageableDto pageableDto, Long employerId);
    Slice<EquipmentRentRecordResDto> getPageableRentEquipments(PageableDto pageableDto);
    MultipleEquipmentsDataDto<RentDetailsResDto> getRentDetails(Long rentId, LoggedUserDataDto loggedUser);
    void calculatePricesForRentEquipments(List<EquipmentRentRecordResDto> equipments, InMemoryRentDataDto rentData,
                                          AddEditEquipmentCartResDto editModal);
    String deleteRent(Long rentId, LoggedUserDataDto loggedUser);
    boolean checkIfRentExist(Long rentId);
    boolean checkIfRentIsFromEmployer(Long rentId, Long employerId);
    UpdatedInMemoryRentData updateAndGetInMemoryRentData(Long customerId, LoggedUserDataDto loggedUser);
    void persistFirstStageRentForm(NewRentDetailsReqDto reqDto, NewRentDetailsResDto resDto,
                                   InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, Long customerId);
    void persistNewRent(InMemoryRentDataDto rentData, LoggedUserDataDto loggedUser, WebServletRequest req);
    String generateIssuedIdentifier(Long customerId, Long employerId);
}
