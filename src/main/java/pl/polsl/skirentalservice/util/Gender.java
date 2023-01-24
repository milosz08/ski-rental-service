/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Gender.java
 *  Last modified: 29/12/2022, 20:55
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.util;

import lombok.*;
import java.util.*;

import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum Gender {
    UNISEX("unisex", 'U'),
    MALE("mężczyzna", 'M'),
    FEMALE("kobieta", 'K');

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String name;
    private final char alias;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public FormSelectTupleDto convertToTuple(Gender gender) {
        return new FormSelectTupleDto(name.equals(gender.name), String.valueOf(alias), name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Gender findByAlias(String alias) {
        return Arrays.stream(Gender.values()).filter(g -> g.alias == alias.charAt(0))
                .findFirst()
                .orElse(Gender.MALE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getGenders() {
        return List.of(
            new FormSelectTupleDto(true, String.valueOf(MALE.alias), MALE.name),
            new FormSelectTupleDto(false, String.valueOf(FEMALE.alias), FEMALE.name)
        );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getSelectedGender(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getSelectedGenderWithUnisex(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender), UNISEX.convertToTuple(gender));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<FormSelectTupleDto> getGendersWithUnisex() {
        final List<FormSelectTupleDto> genders = new ArrayList<>(getGenders());
        genders.add(new FormSelectTupleDto(false, String.valueOf(UNISEX.alias), UNISEX.name));
        return genders;
    }
}
