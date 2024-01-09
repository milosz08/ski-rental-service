/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.lang.reflect.Type;

@Singleton
@Startup
public class ModelMapperBean {
    private final ModelMapper modelMapper;

    public ModelMapperBean() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STANDARD)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
            .setAmbiguityIgnored(true);
    }

    public <T> T map(Object source, Class<T> mapTo) {
        return modelMapper.map(source, mapTo);
    }

    public <T> T map(Object source, Type mapTo) {
        return modelMapper.map(source, mapTo);
    }

    public void map(Object data, Object destination) {
        modelMapper.map(data, destination);
    }

    public void onUpdateNullableTransactTurnOn() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    public void onUpdateNullableTransactTurnOff() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNull());
    }
}
