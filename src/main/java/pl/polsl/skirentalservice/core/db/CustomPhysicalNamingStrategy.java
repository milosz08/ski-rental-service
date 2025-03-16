package pl.polsl.skirentalservice.core.db;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;

import java.util.StringJoiner;

public class CustomPhysicalNamingStrategy extends ImplicitNamingStrategyJpaCompliantImpl {
    @Override
    public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
        return Identifier.toIdentifier(new StringJoiner(StringUtils.EMPTY)
            .add(convertToSnakeCase(source.getAttributePath().getProperty()))
            .add("_")
            .add(convertToSnakeCase(source.getReferencedColumnName().getText()))
            .toString());
    }

    @Override
    public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
        return Identifier.toIdentifier(convertToSnakeCase(source.getAttributePath().getProperty()));
    }

    private String convertToSnakeCase(String propertyName) {
        return propertyName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
