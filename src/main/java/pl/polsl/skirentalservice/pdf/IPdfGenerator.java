/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: IPdfGenerator.java
 *  Last modified: 08/02/2023, 21:03
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.pdf;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IPdfGenerator {
    void generate() throws RuntimeException;
    void remove() throws RuntimeException;
    String getPath();
}
