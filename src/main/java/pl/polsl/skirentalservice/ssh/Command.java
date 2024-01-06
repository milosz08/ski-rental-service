/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.polsl.skirentalservice.core.ssh.SshCommand;

@Getter
@AllArgsConstructor
public enum Command implements SshCommand {
    CREATE_MAILBOX("create-mailbox"),
    UPDATE_MAILBOX_PASSWORD("update-mailbox-password"),
    DELETE_MAILBOX("delete-mailbox"),
    SET_MAILBOX_CAPACITY("set-mailbox-capacity"),
    ;

    private final String executableFor;

    @Override
    public String getCommandName() {
        return executableFor;
    }
}
