package pl.polsl.skirentalservice.dto.attribute;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import pl.polsl.skirentalservice.core.ReqValidatePojo;
import pl.polsl.skirentalservice.core.servlet.WebServletRequest;

@Data
public class AttributeModalReqDto implements ReqValidatePojo {
    @NotEmpty(message = "Pole nie może być puste.")
    @Size(min = 5, max = 50, message = "Pole musi zawierać od 5 do 50 znaków.")
    private String name;

    public AttributeModalReqDto(WebServletRequest req) {
        this.name = StringUtils.trimToEmpty(req.getParameter("name"));
    }

    @Override
    public String toString() {
        return "{" +
            "name=" + name +
            '}';
    }
}
