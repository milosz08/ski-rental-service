package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.Builder;

@Builder
record PaginationData(
    int page,
    int total
) {
}
