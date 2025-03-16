package pl.polsl.skirentalservice.dao;

import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;

import java.util.List;
import java.util.Optional;

public interface ReturnDao {
    Optional<ReturnAlreadyExistPayloadDto> findReturnExistDocument(Object rentId);

    Optional<ReturnRentDetailsResDto> findReturnDetails(Object returnId, Object employerId, String roleAlias);

    Long findAllReturnsCount(FilterDataDto filterData);

    Long findAllReturnsFromEmployerCount(FilterDataDto filterData, Object employerId);

    List<OwnerRentReturnRecordResDto> findAllPageableReturnsRecords(PageableDto pageableDto);

    List<SellerRentReturnRecordResDto> findAllPageableReturnsFromEmployerRecords(PageableDto pageableDto, Object employerId);

    boolean checkIfReturnExist(Object returnId);

    boolean checkIfReturnIsFromEmployer(Object returnId, Object employerId);
}
