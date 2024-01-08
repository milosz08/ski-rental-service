/*
 * Copyright (c) 2023 by MILOSZ GILGA <https://miloszgilga.pl>
 * Silesian University of Technology
 */
package pl.polsl.skirentalservice.core.ssh;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.apache.commons.text.StringSubstitutor;
import pl.polsl.skirentalservice.core.XMLConfigLoader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Slf4j
public class SshSocketSingleton {
    private static final String SSH_CFG = "/ssh/ssh.cfg.xml";
    private static volatile SshSocketSingleton instance;

    private final XMLSshCommands sshCommands;
    private final Properties sshProperties;
    private final Gson gson;

    private SshSocketSingleton() {
        final XMLConfigLoader<XMLSshConfig> configLoader = new XMLConfigLoader<>(SSH_CFG, XMLSshConfig.class);
        sshProperties = configLoader.loadConfig();
        sshCommands = configLoader.getConfigDatalist().getCommands();
        gson = new Gson();
    }

    public <T> T executeCommand(
        Function<XMLSshCommands, String> commandCallback, Map<String, String> entries, Class<T> mapTo
    ) throws RuntimeException {
        final String command = commandCallback.apply(sshCommands);
        final StringSubstitutor substitutor = new StringSubstitutor(entries);
        T mappedTo;
        try (final SSHClient sshClient = new SSHClient()) {
            sshClient.loadKnownHosts(new File(sshProperties.getProperty("ssh.known-hosts.path")));
            sshClient.connect(sshProperties.getProperty("ssh.host"));
            sshClient.authPublickey(sshProperties.getProperty("ssh.login"),
                sshProperties.getProperty("ssh.public-key.path"));
            final Session session = sshClient.startSession();
            final Session.Command sessionCommand = session.exec(substitutor.replace(command));
            final String response = IOUtils.readFully(sessionCommand.getInputStream()).toString();
            sessionCommand.join();
            session.close();
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
        if (instance == null) {
            instance = new SshSocketSingleton();
        }
        return instance;
    }
}
