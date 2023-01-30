/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EquipmentDetailsResDto.java
 *  Last modified: 27/01/2023, 03:47
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.equipment;

import lombok.*;
import java.math.BigDecimal;

import pl.polsl.skirentalservice.util.Gender;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@AllArgsConstructor
public class EquipmentDetailsResDto {
    private Long id;
    private String name;
    private String type;
    private String model;
    private Gender gender;
    private String barcode;
    private String size;
    private String brand;
    private String color;
    private String countData;
    private String rentCount;
    private BigDecimal pricePerHour;
    private BigDecimal priceForNextHour;
    private BigDecimal pricePerDay;
    private BigDecimal valueCost;
    private String description;
}
