/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: ConfigBean.java
 *  Last modified: 26/01/2023, 18:58
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import lombok.Getter;

import jakarta.ejb.Startup;
import jakarta.ejb.Singleton;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@Startup
@Singleton(name = "ConfigFactoryBean")
public class ConfigBean {

    private final String systemVersion;
    private final int circaDateYears;
    private final String defPageTitle;
    private final String uploadsDir;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ConfigBean() throws IOException {
        this.systemVersion = StringUtils.defaultIfEmpty(getClass().getPackage().getImplementationVersion(), "DEVELOPMENT");
        this.circaDateYears = 18;
        this.defPageTitle = "SkiRent System";
        final Path path = Paths.get(System.getProperty("jboss.server.data.dir") + "/ski-rental-service");
        Files.createDirectories(path);
        this.uploadsDir = path.toString();
    }
}
