package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {
    IDLE("IDLE"),
    ASC("ASC"),
    DESC("DESC"),
    ;

    private final String dir;
}
