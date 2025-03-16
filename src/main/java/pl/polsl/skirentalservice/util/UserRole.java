package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserRole {
    USER("użytkownik", 'U', "user"),
    SELLER("pracownik", 'P', "seller"),
    OWNER("kierownik", 'K', "owner"),
    ;

    private final String name;
    private final Character alias;
    private final String eng;
}
