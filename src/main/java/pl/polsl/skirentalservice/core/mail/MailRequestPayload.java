/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class MailRequestPayload {
    private String messageResponder;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVars;
    private Set<String> attachmentsPaths;

    @Override
    public String toString() {
        return "{" +
            "messageResoponder=" + messageResponder +
            ", subject=" + subject +
            ", templateName=" + templateName +
            ", templateVars=" + templateVars +
            ", attachmentsPaths=" + attachmentsPaths +
            '}';
    }
}
