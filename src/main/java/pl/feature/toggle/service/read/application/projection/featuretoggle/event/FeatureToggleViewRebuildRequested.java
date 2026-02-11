package pl.feature.toggle.service.read.application.projection.featuretoggle.event;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;

public record FeatureToggleViewRebuildRequested(
        FeatureToggleId featureToggleId
) {
}
