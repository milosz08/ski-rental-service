/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandPerformException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandPerformException.class);

    public CommandPerformException(String message, String errMsg) {
        super(message + " Spróbuj ponownie później.");
        LOGGER.error("Unable to perform SSH command. Command details: {}. ERR Cause by: {}", message, errMsg);
    }
}
