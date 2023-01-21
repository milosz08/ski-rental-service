/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: Command.java
 *  Last modified: 21/01/2023, 07:27
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.ssh;

import lombok.*;

import pl.polsl.skirentalservice.core.ssh.ICommand;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@AllArgsConstructor
public enum Command implements ICommand {
    CREATE_MAILBOX("create-mailbox"),
    UPDATE_MAILBOX_PASSWORD("update-mailbox-password"),
    DELETE_MAILBOX("delete-mailbox"),
    SET_MAILBOX_CAPACITY("set-mailbox-capacity");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String executableFor;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return executableFor;
    }
}
