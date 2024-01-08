/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import pl.polsl.skirentalservice.core.ConfigSingleton;
import pl.polsl.skirentalservice.core.ssh.CommandPerformException;
import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;
import pl.polsl.skirentalservice.core.ssh.XMLSshCommands;

import java.util.Map;
import java.util.function.Function;

public class ExecCommandPerformer implements ExecCommand {
    private final SshSocketSingleton sshSocket = SshSocketSingleton.getInstance();
    private final ConfigSingleton config = ConfigSingleton.getInstance();

    @Override
    public void createMailbox(String email, String password) throws CommandPerformException {
        performCommand(XMLSshCommands::getCreateMailbox, Map.of("email", email, "password", password),
            "Nieudane utworzenie skrzynki pocztowej pracownika.");
        performCommand(XMLSshCommands::getSetMailboxCapacity, Map.of("email", email),
            "Nieudane ustawienie limitu powierzchni skrzynki pocztowej pracownika.");
    }

    @Override
    public void updateMailboxPassword(String email, String newPassword) throws CommandPerformException {
        performCommand(XMLSshCommands::getUpdateMailboxPassword, Map.of("email", email, "newPassword", newPassword),
            "Nieudane zaktualizowanie hasła skrzynki pocztowej.");
    }

    @Override
    public void deleteMailbox(String email) throws CommandPerformException {
        performCommand(XMLSshCommands::getDeleteMailbox, Map.of("email", email),
            "Nieudane usunięcie skrzynki pocztowej.");
    }

    private void performCommand(
        Function<XMLSshCommands, String> commandCallback, Map<String, String> entries, String errorMessage
    ) {
        if (!config.getEnvironment().isDevOrDocker()) {
            final var createResult = sshSocket.executeCommand(commandCallback, entries, BaseCommandResponse.class);
            if (ReturnCode.isInvalid(createResult)) {
                throw new CommandPerformException(errorMessage, createResult.getMsg());
            }
        }
    }
}
