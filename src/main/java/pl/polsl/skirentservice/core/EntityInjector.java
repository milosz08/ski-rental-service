/*
 * Copyright (c) 2022 by multiple authors
 * Silesian University of Technology
 *
 *  File name: EntityInjector.java
 *  Last modified: 22.12.2022, 21:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentservice.core;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//----------------------------------------------------------------------------------------------------------------------

@Retention(RUNTIME)
@Target(TYPE)
public @interface EntityInjector {
}
