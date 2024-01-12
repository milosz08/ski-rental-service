/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.Builder;

@Builder
record PaginationData(
    int page,
    int total
) {
}
