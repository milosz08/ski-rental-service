package pl.polsl.skirentalservice.core.servlet.pageable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SorterDataDto {
    private Map<String, ServletSorterField> fieldsMap;
    private String jpql;
}
