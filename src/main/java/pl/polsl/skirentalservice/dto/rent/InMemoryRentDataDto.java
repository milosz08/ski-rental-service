/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: InMemoryRentDataDto.java
 *  Last modified: 31/01/2023, 08:24
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.dto.rent;

import lombok.*;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pl.polsl.skirentalservice.util.RentStatus;
import pl.polsl.skirentalservice.dto.PriceUnitsDto;
import pl.polsl.skirentalservice.dto.customer.CustomerDetailsResDto;

import static java.time.LocalDateTime.parse;
import static java.time.temporal.ChronoUnit.MINUTES;

import static pl.polsl.skirentalservice.util.RentStatus.OPENED;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
public class InMemoryRentDataDto {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Long customerId;
    private String customerFullName;
    private String issuedIdentifier;
    private String issuedDateTime;
    private String rentDateTime;
    private String returnDateTime;
    private RentStatus rentStatus;
    private String description;
    private String tax;
    private PriceUnitsDto priceUnits;
    private long days;
    private long hours;
    private int totalCount;
    private boolean allGood;
    private CustomerDetailsResDto customerDetails;
    private List<CartSingleEquipmentDataDto> equipments = new ArrayList<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public InMemoryRentDataDto(Long customerId, String customerFullName) {
        this.customerId = customerId;
        this.customerFullName = customerFullName;
        this.issuedDateTime = LocalDateTime.now().truncatedTo(MINUTES).toString().replace('T', ' ');
        this.rentStatus = OPENED;
        this.priceUnits = new PriceUnitsDto();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public LocalDateTime getParsedRentDateTime() {
        return parse(rentDateTime.replace('T', ' '), formatter);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return parse(returnDateTime.replace('T', ' '), formatter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "customerId=" + customerId +
            ", customerFullName='" + customerFullName +
            ", issuedIdentifier='" + issuedIdentifier +
            ", issuedDateTime='" + issuedDateTime +
            ", rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", rentStatus=" + rentStatus +
            ", description='" + description +
            ", tax='" + tax +
            ", priceUnits=" + priceUnits +
            ", days=" + days +
            ", hours=" + hours +
            ", totalCount=" + totalCount +
            ", allGood=" + allGood +
            ", equipments=" + equipments +
            '}';
    }
}
