/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import pl.polsl.skirentalservice.core.ssh.CommandPerformException;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;

import java.util.Map;

public class ExecCommandPerformer implements IExecCommandPerformer {

    private final SshSocketSingleton sshSocket;

    public ExecCommandPerformer(SshSocketSingleton sshSocket) {
        this.sshSocket = sshSocket;
    }

    @Override
    public void createMailbox(String email, String password) throws CommandPerformException {
        final Map<String, String> entries = Map.of("email", email, "password", password);
        final var createResult = sshSocket.executeCommand(Command.CREATE_MAILBOX, entries, BaseCommandResponse.class);
        final var capacityResult = sshSocket.executeCommand(Command.SET_MAILBOX_CAPACITY, Map.of("email", email),
            BaseCommandResponse.class);
        if (ReturnCode.isInvalid(createResult)) {
            throw new CommandPerformException("Nieudane utworzenie skrzynki pocztowej pracownika.", createResult.getMsg());
        }
        if (ReturnCode.isInvalid(capacityResult)) {
            throw new CommandPerformException("Nieudane ustawienie limitu powierzchni skrzynki pocztowej pracownika.",
                capacityResult.getMsg());
        }
    }

    @Override
    public void updateMailboxPassword(String email, String newPassword) throws CommandPerformException {
        final Map<String, String> entries = Map.of("email", email, "newPassword", newPassword);
        final var response = sshSocket.executeCommand(Command.UPDATE_MAILBOX_PASSWORD, entries, BaseCommandResponse.class);
        if (ReturnCode.isInvalid(response)) {
            throw new CommandPerformException("Nieudane zaktualizowanie hasła skrzynki pocztowej.", response.getMsg());
        }
    }

    @Override
    public void deleteMailbox(String email) throws CommandPerformException {
        final var response = sshSocket.executeCommand(Command.DELETE_MAILBOX, Map.of("email", email), BaseCommandResponse.class);
        if (ReturnCode.isInvalid(response)) {
            throw new CommandPerformException("Nieudane usunięcie skrzynki pocztowej.", response.getMsg());
        }
    }
}
