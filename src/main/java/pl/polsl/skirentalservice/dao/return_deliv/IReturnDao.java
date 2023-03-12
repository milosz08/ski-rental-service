/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ReturnDao.java
 *  Last modified: 20/02/2023, 18:46
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dao.return_deliv;

import java.util.List;
import java.util.Optional;

import pl.polsl.skirentalservice.dto.PageableDto;
import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnRentDetailsResDto;
import pl.polsl.skirentalservice.dto.deliv_return.OwnerRentReturnRecordResDto;
import pl.polsl.skirentalservice.dto.deliv_return.ReturnAlreadyExistPayloadDto;
import pl.polsl.skirentalservice.dto.deliv_return.SellerRentReturnRecordResDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IReturnDao {
    Optional<ReturnAlreadyExistPayloadDto> findReturnExistDocument(Object rentId);
    Optional<ReturnRentDetailsResDto> findReturnDetails(Object returnId, Object employerId, String roleAlias);

    Long findAllReturnsCount(FilterDataDto filterData);
    Long findAllReturnsFromEmployerCount(FilterDataDto filterData, Object employerId);

    List<OwnerRentReturnRecordResDto> findAllPageableReturnsRecords(PageableDto pageableDto);
    List<SellerRentReturnRecordResDto> findAllPageableReturnsFromEmployerRecords(PageableDto pageableDto, Object employerId);
}
