package pl.polsl.skirentalservice.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RentStatus {
    OPENED("otwarty", "opened"),
    RENTED("wypożyczony", "rented"),
    RETURNED("zwrócony", "returned"),
    BOOKED("zarezerwowany", "booked"),
    ;

    private final String status;
    private final String statusEng;
}
