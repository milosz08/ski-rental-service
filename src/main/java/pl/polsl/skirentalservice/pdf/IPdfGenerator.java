/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf;

public interface IPdfGenerator {
    void generate() throws RuntimeException;
    void remove() throws RuntimeException;
    String getPath();
}
