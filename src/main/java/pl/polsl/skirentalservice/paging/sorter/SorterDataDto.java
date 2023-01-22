/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SorterDataDto.java
 *  Last modified: 22/01/2023, 16:05
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.paging.sorter;

import lombok.*;
import java.util.Map;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class SorterDataDto {
    private Map<String, ServletSorterField> fieldsMap;
    private String jpql;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SorterDataDto(Map<String, ServletSorterField> fieldsMap) {
        this.fieldsMap = fieldsMap;
    }
}
