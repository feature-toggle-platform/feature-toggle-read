package pl.feature.toggle.service.read.application.projection.environment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.feature.toggle.service.read.application.port.in.EnvironmentProjection;
import pl.feature.toggle.service.read.application.port.in.EnvironmentViewConsistency;
import pl.feature.toggle.service.read.application.port.out.ConfigurationClient;
import pl.feature.toggle.service.read.application.port.out.EnvironmentProjectionRepository;
import pl.feature.toggle.service.read.application.port.out.EnvironmentQueryRepository;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentProjectionFacade {

    public static EnvironmentProjection environmentProjection() {
        return new EnvironmentProjectionHandler();
    }

    public static EnvironmentViewConsistency environmentViewConsistency(ConfigurationClient configurationClient,
                                                                        EnvironmentProjectionRepository environmentProjectionRepository,
                                                                        EnvironmentQueryRepository environmentQueryRepository) {
        return new DefaultEnvironmentViewConsistency(configurationClient, environmentProjectionRepository, environmentQueryRepository);
    }
}
