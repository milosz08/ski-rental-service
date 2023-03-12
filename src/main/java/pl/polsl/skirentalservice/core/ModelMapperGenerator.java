/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ModelMapperGenerator.java
 *  Last modified: 08/02/2023, 22:18
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ModelMapperGenerator {

    private static final ModelMapper modelMapper = createModelMapper();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static ModelMapper createModelMapper() {
        final ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STANDARD)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
            .setAmbiguityIgnored(true);
        return modelMapper;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void onUpdateNullableTransactTurnOn() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    public static void onUpdateNullableTransactTurnOff() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNull());
    }

    public static ModelMapper getModelMapper() {
        return modelMapper;
    }
}
