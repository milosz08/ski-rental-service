/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import lombok.Builder;
import pl.polsl.skirentalservice.core.servlet.pageable.FilterDataDto;
import pl.polsl.skirentalservice.core.servlet.pageable.SorterDataDto;

@Builder
public record PageableDto(
    FilterDataDto filterData,
    SorterDataDto sorterData,
    int page,
    int total
) {
}
