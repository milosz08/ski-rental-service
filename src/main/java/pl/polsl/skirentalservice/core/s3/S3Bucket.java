/*
 * Copyright (c) 2024 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum S3Bucket {
    BARCODES("barcodes"),
    RENTS("rents"),
    RETURNS("returns"),
    ;

    private final String bucketName;

    public static S3Bucket getBucketName(String bucketName) {
        return Arrays.stream(values())
            .filter(v -> v.bucketName.equals(bucketName))
            .findFirst()
            .orElse(null);
    }
}
