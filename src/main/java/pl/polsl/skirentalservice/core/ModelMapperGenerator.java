/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import lombok.Getter;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

public class ModelMapperGenerator {
    @Getter
    private static final ModelMapper modelMapper = createModelMapper();

    private static ModelMapper createModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STANDARD)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
            .setAmbiguityIgnored(true);
        return modelMapper;
    }

    public static void onUpdateNullableTransactTurnOn() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    public static void onUpdateNullableTransactTurnOff() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNull());
    }
}
