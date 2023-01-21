/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ConfigBean.java
 *  Last modified: 21/01/2023, 03:39
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import lombok.Getter;
import jakarta.ejb.*;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@Startup
@Singleton(name = "ConfigFactoryBean")
public class ConfigBean {

    private final String systemVersion;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ConfigBean() {
        this.systemVersion = defaultIfEmpty(getClass().getPackage().getImplementationVersion(), "DEVELOPMENT");
    }
}
