/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ValidatorFactory.java
 *  Last modified: 27/12/2022, 22:29
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core;

import jakarta.ejb.*;
import jakarta.validation.*;

import java.util.Set;

import pl.polsl.skirentalservice.dto.FormValueInfoTupleDto;

//----------------------------------------------------------------------------------------------------------------------

@Startup
@Singleton(name = "HibernateValidatorFactoryBean")
public class ValidatorFactory {

    private final Validator validator;

    //------------------------------------------------------------------------------------------------------------------

    ValidatorFactory() {
        final jakarta.validation.ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }

    //------------------------------------------------------------------------------------------------------------------

    public <T extends IReqValidatePojo> FormValueInfoTupleDto validateField(T req, String field) {
        return validateField(req, field, "");
    }

    //------------------------------------------------------------------------------------------------------------------

    public <T extends IReqValidatePojo> FormValueInfoTupleDto validateField(T req, String field, String value) {
        final Set<ConstraintViolation<T>> constraints = validator.validateProperty(req, field);
        final FormValueInfoTupleDto resDto = new FormValueInfoTupleDto(value);
        for (ConstraintViolation<T> constraint : constraints) {
            resDto.setErrorStyle("is-invalid");
            resDto.setMessage(constraint.getMessage());
        }
        return resDto;
    }

    //------------------------------------------------------------------------------------------------------------------

    public <T extends IReqValidatePojo> boolean allFieldsIsValid(T req) {
        final Set<ConstraintViolation<T>> constraints = validator.validate(req);
        return constraints.isEmpty();
    }

    //------------------------------------------------------------------------------------------------------------------

    public Validator getValidator() {
        return validator;
    }
}
