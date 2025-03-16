package pl.polsl.skirentalservice.dto.rent;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;
import pl.polsl.skirentalservice.util.DateUtils;
import pl.polsl.skirentalservice.util.Regex;

import java.time.LocalDateTime;

@Data
public class NewRentDetailsReqDto implements ReqValidatePojo {
    @NotEmpty(message = "Pole daty wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DATE_TIME, message = "Nieprawidłowa wartość w polu data wypożyczenia.")
    private String rentDateTime;

    @NotEmpty(message = "Pole daty zwrotu wypożyczenia nie może być puste.")
    @Pattern(regexp = Regex.DATE_TIME, message = "Nieprawidłowa wartość w polu data zwrotu wypożyczenia.")
    private String returnDateTime;

    @NotEmpty(message = "Pole wartości procentowej podatku nie może być puste.")
    @Pattern(regexp = Regex.TAX, message = "Nieprawidłowa wartość w polu wartości procentowej podatku.")
    private String tax;

    @Size(max = 200, message = "Pole dodatkowych uwag do składanego wypożyczenia może mieć maksymalnie 200 znaków.")
    private String description;

    public NewRentDetailsReqDto(WebServletRequest req) {
        this.rentDateTime = StringUtils.trimToEmpty(req.getParameter("rentDateTime"));
        this.returnDateTime = StringUtils.trimToEmpty(req.getParameter("returnDateTime"));
        this.tax = StringUtils.trimToEmpty(req.getParameter("tax")).replace(',', '.');
        this.description = StringUtils.trimToNull(req.getParameter("description"));
    }

    public LocalDateTime getParsedRentDateTime() {
        return DateUtils.parse(rentDateTime);
    }

    public LocalDateTime getParsedReturnDateTime() {
        return DateUtils.parse(returnDateTime);
    }

    @Override
    public String toString() {
        return "{" +
            "rentDateTime='" + rentDateTime +
            ", returnDateTime='" + returnDateTime +
            ", tax='" + tax +
            ", description='" + description +
            '}';
    }
}
