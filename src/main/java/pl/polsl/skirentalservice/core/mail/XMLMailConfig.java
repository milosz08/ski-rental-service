package pl.polsl.skirentalservice.core.mail;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;
import pl.polsl.skirentalservice.core.AbstractXMLProperties;

@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mail-configuration")
class XMLMailConfig extends AbstractXMLProperties {
}
