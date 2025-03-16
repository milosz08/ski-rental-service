package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.Getter;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

@Getter
public class FilterColumn extends FormSelectTupleDto {
    private final String columnName;

    public FilterColumn(String value, String text, String columnName) {
        this(false, value, text, columnName);
    }

    public FilterColumn(boolean isSelected, String value, String text, String columnName) {
        super(isSelected, value, text);
        this.columnName = columnName;
    }
}
