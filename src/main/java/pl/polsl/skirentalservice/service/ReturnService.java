package pl.polsl.skirentalservice.service;

import jakarta.ejb.Local;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.core.servlet.pageable.Slice;
import pl.polsl.skirentalservice.dto.GeneratedReturnData;
import pl.polsl.skirentalservice.dto.MultipleEquipmentsDataDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.login.LoggedUserDataDto;

@Local
public interface ReturnService {
    Slice<OwnerRentReturnRecordResDto> getPageableOwnerReturns(PageableDto pageableDto);

    Slice<SellerRentReturnRecordResDto> getPageableEmployerReturns(PageableDto pageableDto, Long employerId);

    MultipleEquipmentsDataDto<ReturnRentDetailsResDto> getReturnDetails(Long returnId, LoggedUserDataDto loggedUser);

    GeneratedReturnData generateReturn(Long rentId, String description, LoggedUserDataDto loggedUser, WebServletRequest req);

    String deleteReturn(Long returnId, LoggedUserDataDto loggedUser);

    boolean checkIfReturnExist(Long returnId);

    boolean checkIfReturnIsFromEmployer(Long returnId, Long employerId);
}
