/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: ExecCommandPerformer.java
 * Last modified: 6/3/23, 1:15 AM
 * Project name: ski-rental-service
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.polsl.skirentalservice.ssh;

import java.util.Map;

import pl.polsl.skirentalservice.core.ssh.SshSocketSingleton;
import pl.polsl.skirentalservice.core.ssh.CommandPerformException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ExecCommandPerformer implements IExecCommandPerformer {

    private final SshSocketSingleton sshSocket;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecCommandPerformer(SshSocketSingleton sshSocket) {
        this.sshSocket = sshSocket;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateMailboxPassword(String email, String newPassword) throws CommandPerformException {
        final Map<String, String> entries = Map.of("email", email, "newPassword", newPassword);
        final var response = sshSocket.executeCommand(Command.UPDATE_MAILBOX_PASSWORD, entries, BaseCommandResponse.class);
        if (ReturnCode.isInvalid(response)) {
            throw new CommandPerformException("Nieudane zaktualizowanie hasła skrzynki pocztowej.", response.getMsg());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void deleteMailbox(String email) throws CommandPerformException {
        final var response = sshSocket.executeCommand(Command.DELETE_MAILBOX, Map.of("email", email), BaseCommandResponse.class);
        if (ReturnCode.isInvalid(response)) {
            throw new CommandPerformException("Nieudane usunięcie skrzynki pocztowej.", response.getMsg());
        }
    }
}
