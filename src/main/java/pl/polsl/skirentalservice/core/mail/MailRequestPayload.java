/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class MailRequestPayload {
    private String messageResponder;
    private String subject;
    private MailTemplate template;
    private Map<String, Object> templateVars;
    private List<Attachment> attachments;

    @Override
    public String toString() {
        return "{" +
            "messageResoponder=" + messageResponder +
            ", subject=" + subject +
            ", template=" + template +
            ", templateVars=" + templateVars +
            ", attachments=" + attachments +
            '}';
    }
}
