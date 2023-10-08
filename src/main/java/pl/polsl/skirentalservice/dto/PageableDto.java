/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.dto;

import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;

public record PageableDto(
    FilterDataDto filterData,
    SorterDataDto sorterData,
    int page,
    int total
) {
}
