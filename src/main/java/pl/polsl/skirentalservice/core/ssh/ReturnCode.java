package pl.polsl.skirentalservice.core.ssh;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    OK("OK"),
    ERROR("ERROR"),
    ;

    private final String name;

    public static boolean isInvalid(BaseCommandResponse response) {
        return response.getCode().equals(ERROR.name);
    }
}
