/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.db;

import jakarta.ejb.ApplicationException;
import pl.polsl.skirentalservice.core.AbstractAppException;

@ApplicationException(rollback = true)
public class TransactionalException extends AbstractAppException {
    public TransactionalException(Exception exception) {
        super(exception.getMessage());
    }
}
