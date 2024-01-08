/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.s3;

import lombok.Builder;

@Builder
public record FetchedObjectData(
    byte[] data,
    String contentType,
    long contentLength
) {
}
