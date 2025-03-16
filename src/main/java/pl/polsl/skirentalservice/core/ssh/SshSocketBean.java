package pl.polsl.skirentalservice.core.ssh;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import pl.polsl.skirentalservice.core.XMLConfigLoader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

@Slf4j
@Singleton
@Startup
public class SshSocketBean {
    private static final String SSH_CFG = "/ssh/ssh.cfg.xml";

    private final XMLSshCommands sshCommands;
    private final Properties sshProperties;
    private final Gson gson;

    public SshSocketBean() {
        final XMLConfigLoader<XMLSshConfig> configLoader = new XMLConfigLoader<>(SSH_CFG, XMLSshConfig.class);
        sshProperties = configLoader.loadConfig();
        sshCommands = configLoader.getConfigDatalist().getCommands();
        gson = new Gson();
    }

    public <T> T executeCommand(
        Function<XMLSshCommands, String> commandCallback, Map<String, String> entries, Class<T> mapTo
    ) throws RuntimeException {
        String command = commandCallback.apply(sshCommands);
        for (final Map.Entry<String, String> entry : entries.entrySet()) {
            command = command.replace("$[" + entry.getKey() + "]", entry.getValue());
        }
        T mappedTo;
        try (final SSHClient sshClient = new SSHClient()) {
            sshClient.loadKnownHosts(new File(sshProperties.getProperty("ssh.known-hosts.path")));
            sshClient.connect(sshProperties.getProperty("ssh.host"));
            sshClient.authPublickey(sshProperties.getProperty("ssh.login"),
                sshProperties.getProperty("ssh.private-key.path"));
            final Session session = sshClient.startSession();
            final Session.Command sessionCommand = session.exec(command);
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

    public BaseCommandResponse executeCommand(
        Function<XMLSshCommands, String> commandCallback, Map<String, String> entries
    ) throws RuntimeException {
        return executeCommand(commandCallback, entries, BaseCommandResponse.class);
    }
}
