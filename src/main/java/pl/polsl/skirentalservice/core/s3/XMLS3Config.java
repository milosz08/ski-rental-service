package pl.polsl.skirentalservice.core.s3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.AbstractXMLProperties;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "s3-configuration")
class XMLS3Config extends AbstractXMLProperties {
}
