/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: CommandPerformException.java
 *  Last modified: 21/01/2023, 08:35
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import org.slf4j.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class CommandPerformException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandPerformException.class);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CommandPerformException(String message, String errMsg) {
        super(message + " Spróbuj ponownie później.");
        LOGGER.error("Unable to perform SSH command. Command details: {}. ERR Cause by: {}", message, errMsg);
    }
}
