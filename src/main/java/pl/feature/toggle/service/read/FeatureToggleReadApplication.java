package pl.feature.toggle.service.read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FeatureToggleReadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeatureToggleReadApplication.class, args);
    }

}
