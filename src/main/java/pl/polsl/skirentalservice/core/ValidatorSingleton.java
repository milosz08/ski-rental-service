/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import pl.polsl.skirentalservice.dto.FormSelectsDto;
import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

import java.util.Objects;
import java.util.Set;

public class ValidatorSingleton {

    private final Validator validator;
    private static volatile ValidatorSingleton instance;

    private ValidatorSingleton() {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    public <T extends IReqValidatePojo> FormValueInfoTupleDto validateField(T req, String field, String value) {
        final Set<ConstraintViolation<T>> constraints = validator.validateProperty(req, field);
        final FormValueInfoTupleDto resDto = new FormValueInfoTupleDto(value);
        for (final ConstraintViolation<T> constraint : constraints) {
            resDto.setErrorStyle("is-invalid");
            resDto.setMessage(constraint.getMessage());
        }
        return resDto;
    }

    public <T extends IReqValidatePojo> FormSelectsDto validateSelectField(
        T req, String field, FormSelectsDto selectsDto, String selected
    ) {
        final Set<ConstraintViolation<T>> constraints = validator.validateProperty(req, field);
        for (final ConstraintViolation<T> constraint : constraints) {
            selectsDto.setErrorStyle("is-invalid");
            selectsDto.setMessage(constraint.getMessage());
        }
        selectsDto.setSelected(selected);
        return selectsDto;
    }

    public <T extends IReqValidatePojo> boolean someFieldsAreInvalid(T req) {
        final Set<ConstraintViolation<T>> constraints = validator.validate(req);
        return !constraints.isEmpty();
    }

    public Validator getValidator() {
        return validator;
    }

    public static synchronized ValidatorSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ValidatorSingleton();
        }
        return instance;
    }
}
