/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: JAXBSshCommands.java
 *  Last modified: 19/01/2023, 18:17
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import pl.polsl.skirentalservice.core.JAXBNormalizerAdapter;

import static jakarta.xml.bind.annotation.XmlAccessType.FIELD;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(FIELD)
@XmlRootElement(name = "commands")
public class JAXBSshCommands {

    @XmlElement(name = "create-mailbox-command")
    @XmlJavaTypeAdapter(JAXBNormalizerAdapter.class) private String createMailboxCommand;

    @XmlElement(name = "update-mailbox-command")
    @XmlJavaTypeAdapter(JAXBNormalizerAdapter.class) private String updateMailboxCommand;

    @XmlElement(name = "delete-mailbox-command")
    @XmlJavaTypeAdapter(JAXBNormalizerAdapter.class) private String deleteMailboxCommand;

    @XmlElement(name = "set-mailbox-capacity-command")
    @XmlJavaTypeAdapter(JAXBNormalizerAdapter.class) private String setMailboxCapacityCommand;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getCreateMailboxCommand() {
        return createMailboxCommand;
    }

    void setCreateMailboxCommand(String createMailboxCommand) {
        this.createMailboxCommand = createMailboxCommand;
    }

    public String getUpdateMailboxCommand() {
        return updateMailboxCommand;
    }

    void setUpdateMailboxCommand(String updateMailboxCommand) {
        this.updateMailboxCommand = updateMailboxCommand;
    }

    public String getDeleteMailboxCommand() {
        return deleteMailboxCommand;
    }

    void setDeleteMailboxCommand(String deleteMailboxCommand) {
        this.deleteMailboxCommand = deleteMailboxCommand;
    }

    public String getSetMailboxCapacityCommand() {
        return setMailboxCapacityCommand;
    }

    void setSetMailboxCapacityCommand(String setMailboxCapacityCommand) {
        this.setMailboxCapacityCommand = setMailboxCapacityCommand;
    }
}
