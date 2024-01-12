/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.pdf.dto;

import lombok.Builder;
import org.apache.http.entity.ContentType;

@Builder
public record GeneratedPdfData(
    String filename,
    byte[] pdfData,
    ContentType type
) {
}
