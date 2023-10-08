/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Getter
public class ConfigSingleton {

    private final String systemVersion;
    private final int circaDateYears;
    private final String defPageTitle;
    private final String uploadsDir;

    private static volatile ConfigSingleton instance;

    private ConfigSingleton() throws IOException {
        this.systemVersion = StringUtils.defaultIfEmpty(getClass().getPackage().getImplementationVersion(), "DEVELOPMENT");
        this.circaDateYears = 18;
        this.defPageTitle = "SkiRent System";
        final Path path = Paths.get(System.getProperty("catalina.base") + "/uploads/ski-rental-service");
        Files.createDirectories(path);
        this.uploadsDir = path.toString();
    }

    public static synchronized ConfigSingleton getInstance() {
        if (Objects.isNull(instance)) {
            try {
                instance = new ConfigSingleton();
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
        return instance;
    }
}
