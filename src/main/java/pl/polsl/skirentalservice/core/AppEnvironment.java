package pl.polsl.skirentalservice.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppEnvironment {
    DEV("dev"),
    DOCKER("docker"),
    PROD("prod"),
    ;

    private final String environment;

    public static AppEnvironment loadEnviroment() {
        AppEnvironment current;
        try {
            current = valueOf(System.getenv("SKI_ENVIRONMENT"));
        } catch (IllegalArgumentException ignored) {
            current = AppEnvironment.DEV;
        }
        return current;
    }

    public boolean isDevOrDocker() {
        return equals(DEV) || equals(DOCKER);
    }
}
