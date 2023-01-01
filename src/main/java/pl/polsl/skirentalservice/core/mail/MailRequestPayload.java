/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: MailRequestPayload.java
 *  Last modified: 01/01/2023, 18:01
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.mail;

import lombok.*;
import java.util.*;

//----------------------------------------------------------------------------------------------------------------------

@Data
@Builder
@AllArgsConstructor
public class MailRequestPayload {
    private String subject;
    private String templateName;
    private Map<String, String> templateVars;
    private Set<String> attachmentsPaths;

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "{" +
                "subject='" + subject + '\'' +
                ", templateName='" + templateName + '\'' +
                ", templateVars=" + templateVars +
                ", attachmentsPaths=" + attachmentsPaths +
                '}';
    }
}
