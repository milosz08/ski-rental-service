/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.ssh;

import pl.polsl.skirentalservice.core.ssh.CommandPerformException;

public interface ExecCommand {
    void createMailbox(String email, String password) throws CommandPerformException;
    void updateMailboxPassword(String email, String newPassword) throws CommandPerformException;
    void deleteMailbox(String email) throws CommandPerformException;
}
