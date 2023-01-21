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

import org.modelmapper.ModelMapper;
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
            .setFieldAccessLevel(Configuration.AccessLevel.PUBLIC)
            .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
            .setFieldAccessLevel(Configuration.AccessLevel.PROTECTED)
            .setFieldAccessLevel(Configuration.AccessLevel.PACKAGE_PRIVATE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> T map(Object from, Class<T> to) {
        return modelMapper.map(from, to);
    }
}
