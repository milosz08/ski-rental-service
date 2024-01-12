/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.servlet.pageable;

import pl.polsl.skirentalservice.util.SessionAttribute;

public record PageableAttributes(
    SessionAttribute sorter,
    SessionAttribute filter
) {
}
