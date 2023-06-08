/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ConfigSingleton.java
 * Last modified: 6/3/23, 4:29 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.core;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
public class ConfigSingleton {

    private final String systemVersion;
    private final int circaDateYears;
    private final String defPageTitle;
    private final String uploadsDir;

    private static volatile ConfigSingleton instance;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ConfigSingleton() throws IOException {
        this.systemVersion = StringUtils.defaultIfEmpty(getClass().getPackage().getImplementationVersion(), "DEVELOPMENT");
        this.circaDateYears = 18;
        this.defPageTitle = "SkiRent System";
        final Path path = Paths.get(System.getProperty("catalina.base") + "/uploads/ski-rental-service");
        Files.createDirectories(path);
        this.uploadsDir = path.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
