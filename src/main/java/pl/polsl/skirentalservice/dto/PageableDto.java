/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: PageableDto.java
 *  Last modified: 21/02/2023, 21:17
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto;

import pl.polsl.skirentalservice.paging.filter.FilterDataDto;
import pl.polsl.skirentalservice.paging.sorter.SorterDataDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public record PageableDto(
    FilterDataDto filterData,
    SorterDataDto sorterData,
    int page,
    int total
) {
}
