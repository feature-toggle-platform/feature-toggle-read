package pl.feature.toggle.service.read.infrastructure.in.rest.view;

public record FeatureToggleView(
        String id,
        String value
) {
    public static FeatureToggleView from(pl.feature.toggle.service.read.domain.FeatureToggleView featureToggleView) {
        return new FeatureToggleView(
                featureToggleView.id().idAsString(),
                featureToggleView.value().asText()
        );
    }
}
