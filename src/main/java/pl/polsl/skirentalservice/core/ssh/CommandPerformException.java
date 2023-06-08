/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CommandPerformException.java
 * Last modified: 3/12/23, 11:01 AM
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

package pl.polsl.skirentalservice.core.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandPerformException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandPerformException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandPerformException(String message, String errMsg) {
        super(message + " Spróbuj ponownie później.");
        LOGGER.error("Unable to perform SSH command. Command details: {}. ERR Cause by: {}", message, errMsg);
    }
}
