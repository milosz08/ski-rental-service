package pl.polsl.skirentalservice.dto;

import pl.polsl.skirentalservice.dto.rent.InMemoryRentDataDto;

public record BriefTimeData(
    long allHours,
    long days
) {
    public BriefTimeData(InMemoryRentDataDto rentData) {
        this(rentData.getHours(), rentData.getDays());
    }
}
