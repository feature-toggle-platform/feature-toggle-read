package pl.feature.toggle.service.read.infrastructure.in.rest.view;

import pl.feature.toggle.service.read.domain.FeatureToggle;

public record FeatureToggleView(
        String id,
        String value
) {
    public static FeatureToggleView from(FeatureToggle featureToggle) {
        return new FeatureToggleView(
                featureToggle.id().idAsString(),
                featureToggle.value().stringValue()
        );
    }
}
