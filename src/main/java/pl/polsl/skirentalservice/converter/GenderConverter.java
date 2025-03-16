package pl.polsl.skirentalservice.converter;

import jakarta.persistence.AttributeConverter;
import pl.polsl.skirentalservice.util.Gender;

public class GenderConverter implements AttributeConverter<Gender, String> {
    @Override
    public String convertToDatabaseColumn(Gender gender) {
        return gender.getName();
    }

    @Override
    public Gender convertToEntityAttribute(String name) {
        return name == null ? Gender.MALE : switch (name) {
            case "kobieta" -> Gender.FEMALE;
            case "mężczyzna" -> Gender.MALE;
            default -> Gender.UNISEX;
        };
    }
}
