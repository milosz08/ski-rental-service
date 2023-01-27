/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EquipmentRecordResDto.java
 *  Last modified: 26/01/2023, 21:11
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import lombok.*;
import java.math.BigDecimal;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class EquipmentRecordResDto {
    private Long id;
    private String name;
    private String type;
    private String barcode;
    private Integer countInStore;
    private BigDecimal pricePerHour;
    private BigDecimal priceForNextHour;
    private BigDecimal pricePerDay;
    private BigDecimal valueCost;
}
