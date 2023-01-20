/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: ExecCommandPerformer.java
 *  Last modified: 19/01/2023, 18:56
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import java.util.*;
import org.slf4j.*;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ExecCommandPerformer implements IExecCommandPerformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecCommandPerformer.class);

    private final SshSocketBean sshSocket;
    private final JAXBSshCommands commands;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecCommandPerformer(SshSocketBean sshSocket, JAXBSshCommands commands) {
        this.sshSocket = sshSocket;
        this.commands = commands;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void createMailbox(String email, String password) {
        final String createCommand = commands.getCreateMailboxCommand();
        final String capacityCommand = commands.getSetMailboxCapacityCommand();
        final Map<String, String> entries = new HashMap<>();
        entries.put("email", email);
        entries.put("password", password);
        final String createCommandExec = sshSocket.replaceConcurrentCommandEntries(entries, createCommand);
        final String setCapacityCommandExec = sshSocket.replaceConcurrentCommandEntries(entries, capacityCommand);
        final String respo = sshSocket.executeCommand(createCommandExec);
        final String resp2o = sshSocket.executeCommand(setCapacityCommandExec);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void updateMailbox(String email, String newPassword) {
        final String command = commands.getUpdateMailboxCommand();
        final Map<String, String> entries = new HashMap<>();
        entries.put("email", email);
        entries.put("newPassword", newPassword);
        final String updateCommandExec = sshSocket.replaceConcurrentCommandEntries(entries, command);
        sshSocket.executeCommand(updateCommandExec);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void deleteMailbox(String email) {
        final String command = commands.getUpdateMailboxCommand();
        final Map<String, String> entries = new HashMap<>();
        entries.put("email", email);
        final String deleteCommandExec = sshSocket.replaceConcurrentCommandEntries(entries, command);
        sshSocket.executeCommand(deleteCommandExec);
    }
}
