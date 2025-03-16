package pl.polsl.skirentalservice.dto.change_password;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.ValidatorBean;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

@Data
@NoArgsConstructor
public class ChangeForgottenPasswordResDto {
    private FormValueInfoTupleDto password;
    private FormValueInfoTupleDto passwordRepeat;

    public ChangeForgottenPasswordResDto(ValidatorBean validator, ChangeForgottenPasswordReqDto reqDto) {
        this.password = validator.validateField(reqDto, "password", reqDto.getPassword());
        this.passwordRepeat = validator.validateField(reqDto, "passwordRepeat", reqDto.getPasswordRepeat());
    }

    @Override
    public String toString() {
        return '{' +
            "password=" + password +
            ", passwordRepeat=" + passwordRepeat +
            '}';
    }
}
