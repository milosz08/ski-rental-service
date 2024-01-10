/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

public abstract class AbstractAppException extends RuntimeException {
    public AbstractAppException(String message) {
        super(message);
    }

    public AbstractAppException(Throwable cause) {
        super(cause);
    }
}
