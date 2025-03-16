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
