package pl.polsl.skirentalservice.dto;

import pl.polsl.skirentalservice.dto.rent.RentEquipmentsDetailsResDto;

import java.util.List;

public record MultipleEquipmentsDataDto<D>(
    D details,
    List<RentEquipmentsDetailsResDto> relatedElements,
    int totalCountOfRelatedElements
) {
}
