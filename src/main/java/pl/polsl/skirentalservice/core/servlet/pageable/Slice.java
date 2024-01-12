/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet.pageable;

import java.util.List;

public record Slice<T>(
    ServletPagination pagination,
    List<T> elements
) {
    public Slice(ServletPagination pagination) {
        this(pagination, List.of());
    }
}
