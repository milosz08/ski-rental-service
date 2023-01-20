/*
 * Copyright (c) 2023 by multiple authors
 * Silesian University of Technology
 *
 *  File name: SshSocketBean.java
 *  Last modified: 19/01/2023, 18:22
 *  Project name: ski-rental-service
 *
 * This project was written for the purpose of a subject taken in the study of Computer Science.
 * This project is not commercial in any way and does not represent a viable business model
 * of the application. Project created for educational purposes only.
 */

package pl.polsl.skirentalservice.core.ssh;

import org.slf4j.*;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import org.apache.commons.text.StringSubstitutor;
import net.schmizz.sshj.connection.channel.direct.Session;

import jakarta.ejb.*;
import jakarta.xml.bind.*;

import java.io.*;
import java.util.Map;
import java.util.Objects;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Startup
@Singleton(name = "SshSocketFactoryBean")
public class SshSocketBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshSocketBean.class);
    private static final String SSH_CFG = "/ssh/ssh.cfg.xml";

    private JAXBSshCommands commands;
    private JAXBSshProperties properties;
    private String commadPerformerClassName;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SshSocketBean() {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBSshConfig.class);
            final var config = (JAXBSshConfig) jaxbContext.createUnmarshaller()
                .unmarshal(getClass().getResource(SSH_CFG));
            this.properties = config.getProperties();
            this.commands = config.getCommands();
            this.commadPerformerClassName = config.getPerformedClass();
            LOGGER.info("Successful loaded Ssh Socket properties with authentication. Props: {}", properties);
            LOGGER.info("Successful loaded Ssh Socket commands. Commands: {}", commands);
        } catch (Exception ex) {
            LOGGER.error("Unable to load Ssh socket properties from extended XML file: {}", SSH_CFG);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String executeCommand(String command) {
        String response = "";
        final String knownHosts = Objects.requireNonNull(getClass().getResource(properties.getSshKnownHosts())).getFile();
        final String rsaKey = Objects.requireNonNull(getClass().getResource(properties.getSshRsa())).getFile();
        try (final SSHClient sshClient = new SSHClient()) {
            sshClient.loadKnownHosts(new File(knownHosts));
            sshClient.connect(properties.getSshHost());
            sshClient.authPublickey(properties.getSshLogin(), rsaKey);
            final Session session = sshClient.startSession();
            final Session.Command sessionCommand = session.exec(command);
            response = IOUtils.readFully(sessionCommand.getInputStream()).toString();
            sessionCommand.join();
            session.close();
        } catch (IOException ex) {
            LOGGER.error("Unable to perform Ssh socket command operation. Exception: {}", ex.getMessage());
        }
        return response;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    String replaceConcurrentCommandEntries(Map<String, String> entries, String templateCommand) {
        final StringSubstitutor substitutor = new StringSubstitutor(entries);
        return substitutor.replace(templateCommand);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public IExecCommandPerformer getCommandPerformer() {
        try {
            return (IExecCommandPerformer) Class.forName(commadPerformerClassName)
                .getDeclaredConstructor(SshSocketBean.class, JAXBSshCommands.class)
                .newInstance(this, commands);
        } catch (Exception ex) {
            throw new RuntimeException("Unable to instantation class of IExecCommadPerformer interface:" + ex.getMessage());
        }
    }
}
