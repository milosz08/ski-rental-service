package pl.polsl.skirentalservice.dto.attribute;

import lombok.Builder;
import pl.polsl.skirentalservice.dto.AlertTupleDto;

@Builder
public record AttributeValidatorPayloadDto(
    AlertTupleDto alert,
    AttributeModalReqDto reqDto,
    AttributeModalResDto resDto,
    boolean isInvalid
) {
}
