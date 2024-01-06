/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import jakarta.xml.bind.JAXBContext;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SshSocketSingleton {
    private static final String SSH_CFG = "/ssh/ssh.cfg.xml";

    private JAXBSshConfig config;
    private static volatile SshSocketSingleton instance;

    private SshSocketSingleton() {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(JAXBSshConfig.class);
            this.config = (JAXBSshConfig) jaxbContext.createUnmarshaller().unmarshal(getClass().getResource(SSH_CFG));
            log.info("Successful loaded Ssh Socket properties with authentication. Props: {}", config.getProperties());
            log.info("Successful loaded Ssh Socket commands. Commands: {}", config.getCommands());
        } catch (Exception ex) {
            log.error("Unable to load Ssh socket properties from extended XML file: {}", SSH_CFG);
        }
    }

    public <T> T executeCommand(SshCommand com, Map<String, String> entries, Class<T> mapTo) throws RuntimeException {
        final JAXBSshCommand command = config.getCommands().getCommands().stream()
            .filter(c -> c.getExecutableFor().equals(com.getCommandName())).findFirst()
            .orElseThrow(() -> new RuntimeException("Command with executable for " + com.getCommandName()
                + " does not exist in XML scope."));
        final StringSubstitutor substitutor = new StringSubstitutor(entries);
        final JAXBSshProperties properties = config.getProperties();
        final String knownHosts = Objects.requireNonNull(getClass().getResource(properties.getSshKnownHosts())).getFile();
        final String rsaKey = Objects.requireNonNull(getClass().getResource(properties.getSshRsa())).getFile();
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
            log.error("Unable to perform Ssh socket command operation. Exception: {}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        return mappedTo;
    }

    public static synchronized SshSocketSingleton getInstance() {
        if (Objects.isNull(instance)) {
            instance = new SshSocketSingleton();
        }
        return instance;
    }
}
