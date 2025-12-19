package pl.feature.toggle.service.read;

import org.testcontainers.containers.GenericContainer;

class RedisContainer {

    public static final int PORT = 6379;

    private static final GenericContainer<?> INSTANCE =
            new GenericContainer<>("redis:8.4.0")
                    .withExposedPorts(PORT);

    static {
        INSTANCE.start();
    }

    static GenericContainer<?> getInstance() {
        return INSTANCE;
    }

}
