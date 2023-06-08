/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ValidatorSingleton.java
 * Last modified: 6/2/23, 11:51 PM
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

import java.util.Set;
import java.util.Objects;

import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;

import pl.polsl.skirentalservice.dto.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ValidatorSingleton {

    private final Validator validator;
    private static volatile ValidatorSingleton instance;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ValidatorSingleton() {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T extends IReqValidatePojo> FormValueInfoTupleDto validateField(T req, String field, String value) {
        final Set<ConstraintViolation<T>> constraints = validator.validateProperty(req, field);
        final FormValueInfoTupleDto resDto = new FormValueInfoTupleDto(value);
        for (final ConstraintViolation<T> constraint : constraints) {
            resDto.setErrorStyle("is-invalid");
            resDto.setMessage(constraint.getMessage());
        }
        return resDto;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T extends IReqValidatePojo> boolean someFieldsAreInvalid(T req) {
        final Set<ConstraintViolation<T>> constraints = validator.validate(req);
        return !constraints.isEmpty();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Validator getValidator() {
        return validator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static synchronized ValidatorSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ValidatorSingleton();
        }
        return instance;
    }
}
