/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: MailRequestPayload.java
 *  Last modified: 07/02/2023, 12:53
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.mail;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Data
@Builder
@AllArgsConstructor
public class MailRequestPayload {
    private String messageResponder;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVars;
    private Set<String> attachmentsPaths;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "{" +
            "messageResoponder='" + messageResponder +
            ", subject='" + subject +
            ", templateName='" + templateName +
            ", templateVars=" + templateVars +
            ", attachmentsPaths=" + attachmentsPaths +
            '}';
    }
}
