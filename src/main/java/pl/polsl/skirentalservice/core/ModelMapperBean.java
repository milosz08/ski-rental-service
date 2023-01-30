/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ModelMapperFactory.java
 *  Last modified: 19/01/2023, 06:15
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import jakarta.ejb.*;

import org.modelmapper.*;
import org.modelmapper.convention.*;
import org.modelmapper.config.Configuration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Startup
@Singleton(name = "ModelMapperFactoryBean")
public class ModelMapperBean {

    private final ModelMapper modelMapper;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ModelMapperBean() {
        this.modelMapper = new ModelMapper();
        this.modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STANDARD)
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
            .setAmbiguityIgnored(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> T map(Object from, Class<T> to) {
        return modelMapper.map(from, to);
    }

    public void shallowCopy(Object from, Object to) {
        modelMapper.map(from, to);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onUpdateNullableTransactTurnOn() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }

    public void onUpdateNullableTransactTurnOff() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNull());
    }
}
