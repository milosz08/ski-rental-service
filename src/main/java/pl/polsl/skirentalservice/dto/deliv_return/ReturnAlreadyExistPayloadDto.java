package pl.polsl.skirentalservice.dto.deliv_return;

public record ReturnAlreadyExistPayloadDto(
    Long id,
    String returnIdentifier
) {
}
