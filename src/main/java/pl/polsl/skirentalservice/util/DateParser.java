/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateParser {
    private static final DateTimeFormatter DTF_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDateTime parse(String dateStr) {
        return LocalDateTime.parse(dateStr.replace('T', ' '), DTF_FORMATTER);
    }

    public static LocalDate parseToDateOnly(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
}
