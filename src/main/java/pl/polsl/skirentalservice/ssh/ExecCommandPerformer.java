/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import pl.polsl.skirentalservice.core.ssh.CommandPerformException;
import pl.polsl.skirentalservice.core.ssh.JAXBSshCommands;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;

import java.util.Map;
import java.util.function.Function;

public class ExecCommandPerformer implements ExecCommand {
    private final SshSocketSingleton sshSocket = SshSocketSingleton.getInstance();

    @Override
    public void createMailbox(String email, String password) throws CommandPerformException {
        performCommand(JAXBSshCommands::getCreateMailbox, Map.of("email", email, "password", password),
            "Nieudane utworzenie skrzynki pocztowej pracownika.");
        performCommand(JAXBSshCommands::getSetMailboxCapacity, Map.of("email", email),
            "Nieudane ustawienie limitu powierzchni skrzynki pocztowej pracownika.");
    }

    @Override
    public void updateMailboxPassword(String email, String newPassword) throws CommandPerformException {
        performCommand(JAXBSshCommands::getUpdateMailboxPassword, Map.of("email", email, "newPassword", newPassword),
            "Nieudane zaktualizowanie hasła skrzynki pocztowej.");
    }

    @Override
    public void deleteMailbox(String email) throws CommandPerformException {
        performCommand(JAXBSshCommands::getDeleteMailbox, Map.of("email", email),
            "Nieudane usunięcie skrzynki pocztowej.");
    }

    private void performCommand(
        Function<JAXBSshCommands, String> commandCallback, Map<String, String> entries, String errorMessage
    ) {
        final var createResult = sshSocket.executeCommand(commandCallback, entries, BaseCommandResponse.class);
        if (ReturnCode.isInvalid(createResult)) {
            throw new CommandPerformException(errorMessage, createResult.getMsg());
        }
    }
}
