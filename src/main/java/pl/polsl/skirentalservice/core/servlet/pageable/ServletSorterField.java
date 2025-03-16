package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.Getter;

@Getter
public class ServletSorterField {
    private final String jpql;
    private SortDirection direction;
    private String chevronBts;

    public ServletSorterField(String jpql) {
        this.jpql = jpql;
        this.direction = SortDirection.IDLE;
        this.chevronBts = "down-up text-secondary";
    }

    String setAscending() {
        direction = SortDirection.ASC;
        chevronBts = "down";
        return jpql + " ASC";
    }

    String setDescending() {
        direction = SortDirection.DESC;
        chevronBts = "up";
        return jpql + " DESC";
    }

    String reset(String defColumn) {
        direction = SortDirection.IDLE;
        chevronBts = "down-up text-secondary";
        return defColumn + " ASC";
    }
}
