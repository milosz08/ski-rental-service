/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
@Getter
public class ConfigSingleton {
    private final AppEnvironment environment;
    private final String serverHome;
    private final String systemVersion;
    private final String titlePageTag;
    private final int maturityAge;

    private static volatile ConfigSingleton instance;
    private static final String SERVER_CFG = "/server.cfg.xml";

    private ConfigSingleton() {
        environment = AppEnvironment.loadEnviroment();
        log.info("Successful loaded app environment: {}", environment);

        final XMLConfigLoader<XMLServerConfig> configLoader = new XMLConfigLoader<>(SERVER_CFG, XMLServerConfig.class);
        final Properties properties = configLoader.loadConfig();

        serverHome = properties.getProperty("ski.server-home");
        systemVersion = properties.getProperty("ski.system-version");
        titlePageTag = properties.getProperty("ski.title-page-tag");
        maturityAge = Integer.parseInt(properties.getProperty("ski.maturity-age"));
    }

    public static synchronized ConfigSingleton getInstance() {
        if (instance == null) {
            instance = new ConfigSingleton();
        }
        return instance;
    }
}
