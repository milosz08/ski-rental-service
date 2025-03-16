package pl.polsl.skirentalservice.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class XMLConfigLoader<T extends AbstractXMLProperties> {
    private final String configFile;
    private final Class<T> xmlPropertiesClazz;
    @Getter
    private T configDatalist;

    public static void replaceAllPlaceholders(Properties allProperties) {
        for (final Map.Entry<Object, Object> property : allProperties.entrySet()) {
            final String name = (String) property.getKey();
            final String value = (String) property.getValue();
            if (!value.startsWith("${") || !value.endsWith("}")) {
                continue;
            }
            final String envValue = System.getenv(StringUtils.substringBetween(value, "${", "}"));
            if (envValue == null) {
                throw new IllegalStateException("Missing " + name + " required environment variable");
            }
            property.setValue(envValue);
        }
    }

    public Properties loadConfig() {
        Properties properties = new Properties();
        try {
            final Object configData = JAXBContext.newInstance(xmlPropertiesClazz)
                .createUnmarshaller()
                .unmarshal(getClass().getResource(configFile));
            configDatalist = xmlPropertiesClazz.cast(configData);
            properties = configDatalist.mapToProperties();
            replaceAllPlaceholders(properties);
            log.info("Successful loaded config for {} class. Config data: {}",
                xmlPropertiesClazz.getName(), properties);
        } catch (JAXBException ex) {
            log.error("Unable to load XML config from config file: {}", configFile);
        }
        return properties;
    }
}
