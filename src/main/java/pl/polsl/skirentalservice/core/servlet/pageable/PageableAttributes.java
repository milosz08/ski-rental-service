package pl.polsl.skirentalservice.core.servlet.pageable;

import pl.polsl.skirentalservice.util.SessionAttribute;

public record PageableAttributes(
    SessionAttribute sorter,
    SessionAttribute filter
) {
}
