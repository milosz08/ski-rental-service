/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.exception;

import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;
import pl.polsl.skirentalservice.entity.RentEntity;

public class RentException {
    @Slf4j
    public static class RentHasReturnException extends AbstractAppException {
        public RentHasReturnException(RentEntity rent) {
            super("Usunięcie wypożyczenia ze statusem <strong>wypożyczone</strong> nie jest możliwe. Aby usunąć " +
                "niechciane wypożyczenie, usuń przypisany do niego dokument zwrotu.");
            log.warn("Attempt to remove rent with return status. Rent data: {}", rent);
        }
    }
}
