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
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.commons.text.StringSubstitutor;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;

import jakarta.ejb.*;
import jakarta.xml.bind.*;

import java.io.*;
import java.util.Map;

import static java.util.Objects.requireNonNull;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Startup
@Singleton(name = "SshSocketFactoryBean")
public class SshSocketBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SshSocketBean.class);
    private static final String SSH_CFG = "/ssh/ssh.cfg.xml";

    private JAXBSshConfig config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    SshSocketBean() {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBSshConfig.class);
            this.config = (JAXBSshConfig) jaxbContext.createUnmarshaller().unmarshal(getClass().getResource(SSH_CFG));
            LOGGER.info("Successful loaded Ssh Socket properties with authentication. Props: {}", config.getProperties());
            LOGGER.info("Successful loaded Ssh Socket commands. Commands: {}", config.getCommands());
        } catch (Exception ex) {
            LOGGER.error("Unable to load Ssh socket properties from extended XML file: {}", SSH_CFG);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public <T> T executeCommand(ICommand com, Map<String, String> entries, Class<T> mapTo) throws RuntimeException {
        final JAXBSshCommand command = config.getCommands().getCommands().stream()
            .filter(c -> c.getExecutableFor().equals(com.getCommandName())).findFirst()
            .orElseThrow(() -> {
                throw new RuntimeException("Command with executable for " + com.getCommandName()
                    + " does not exist in XML scope.");
            });
        final StringSubstitutor substitutor = new StringSubstitutor(entries);
        final JAXBSshProperties properties = config.getProperties();
        final String knownHosts = requireNonNull(getClass().getResource(properties.getSshKnownHosts())).getFile();
        final String rsaKey = requireNonNull(getClass().getResource(properties.getSshRsa())).getFile();
        T mappedTo;
        try (final SSHClient sshClient = new SSHClient()) {
            sshClient.loadKnownHosts(new File(knownHosts));
            sshClient.connect(properties.getSshHost());
            sshClient.authPublickey(properties.getSshLogin(), rsaKey);
            final Session session = sshClient.startSession();
            final Session.Command sessionCommand = session.exec(substitutor.replace(command.getExecScript()));
            final String response = IOUtils.readFully(sessionCommand.getInputStream()).toString();
            sessionCommand.join();
            session.close();
            final Gson gson = new Gson();
            try (JsonReader jsonReader = new JsonReader(new StringReader(response))) {
                mappedTo = gson.fromJson(jsonReader, mapTo);
            }
        } catch (IOException ex) {
            LOGGER.error("Unable to perform Ssh socket command operation. Exception: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        return mappedTo;
    }
}
