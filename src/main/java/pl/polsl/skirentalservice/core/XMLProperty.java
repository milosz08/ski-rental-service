package pl.polsl.skirentalservice.core;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "property")
public class XMLProperty {
    @XmlAttribute(name = "name")
    private String name;

    @XmlValue
    private String value;
}
