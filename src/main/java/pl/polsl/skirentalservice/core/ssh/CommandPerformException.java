package pl.polsl.skirentalservice.core.ssh;

import lombok.extern.slf4j.Slf4j;
import pl.polsl.skirentalservice.core.AbstractAppException;

@Slf4j
public class CommandPerformException extends AbstractAppException {
    public CommandPerformException(String message, String errMsg) {
        super(message + " Spróbuj ponownie później.");
        log.error("Unable to perform SSH command. Command details: {}. ERR Cause by: {}", message, errMsg);
    }
}
