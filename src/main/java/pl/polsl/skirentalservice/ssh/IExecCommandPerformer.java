/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 * Silesian University of Technology
 *
 *  File name: IExecCommandPerformer.java
 *  Last modified: 21/01/2023, 09:50
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.ssh;

import pl.polsl.skirentalservice.core.ssh.CommandPerformException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IExecCommandPerformer {
    void createMailbox(String email, String password) throws CommandPerformException;
    void updateMailboxPassword(String email, String newPassword) throws CommandPerformException;
    void deleteMailbox(String email) throws CommandPerformException;
}
