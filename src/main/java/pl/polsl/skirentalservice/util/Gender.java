package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.polsl.skirentalservice.dto.FormSelectTupleDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum Gender {
    MALE("mężczyzna", 'M'),
    FEMALE("kobieta", 'K'),
    UNISEX("unisex", 'U'),
    ;

    private final String name;
    private final char alias;

    public static Gender findByAlias(String alias) {
        return Arrays.stream(Gender.values()).filter(g -> g.alias == alias.charAt(0)).findFirst().orElse(Gender.MALE);
    }

    public static List<FormSelectTupleDto> getGenders() {
        return List.of(
            new FormSelectTupleDto(true, String.valueOf(MALE.alias), MALE.name),
            new FormSelectTupleDto(false, String.valueOf(FEMALE.alias), FEMALE.name)
        );
    }

    public static List<FormSelectTupleDto> getSelectedGender(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender));
    }

    public static List<FormSelectTupleDto> getSelectedGenderWithUnisex(Gender gender) {
        return List.of(MALE.convertToTuple(gender), FEMALE.convertToTuple(gender), UNISEX.convertToTuple(gender));
    }

    public static List<FormSelectTupleDto> getGendersWithUnisex() {
        final List<FormSelectTupleDto> genders = new ArrayList<>(getGenders());
        genders.add(new FormSelectTupleDto(false, String.valueOf(UNISEX.alias), UNISEX.name));
        return genders;
    }

    public FormSelectTupleDto convertToTuple(Gender gender) {
        return new FormSelectTupleDto(name.equals(gender.name), String.valueOf(alias), name);
    }
}
