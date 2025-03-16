package pl.polsl.skirentalservice.core.mail;

import org.apache.http.entity.ContentType;

public record Attachment(
    String name,
    byte[] data,
    ContentType type
) {
}
