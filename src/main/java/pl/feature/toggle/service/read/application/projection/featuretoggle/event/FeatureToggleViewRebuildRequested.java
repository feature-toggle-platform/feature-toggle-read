package pl.feature.toggle.service.read.application.projection.featuretoggle.event;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.model.security.correlation.CorrelationId;

public record FeatureToggleViewRebuildRequested(
        FeatureToggleId featureToggleId,
        CorrelationId correlationId
) {
}
