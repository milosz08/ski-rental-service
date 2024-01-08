/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppEnvironment {
    DEV("dev"),
    DOCKER("docker"),
    PROD("prod"),
    ;

    private final String environment;

    public boolean isDevOrDocker() {
        return equals(DEV) || equals(DOCKER);
    }

    public static AppEnvironment loadEnviroment() {
        AppEnvironment current;
        try {
            current = valueOf(System.getenv("SKI_ENVIRONMENT"));
        } catch (IllegalArgumentException ignored) {
            current = AppEnvironment.DEV;
        }
        return current;
    }
}
