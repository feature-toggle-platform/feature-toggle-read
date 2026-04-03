package com.configly.read.application.projection.featuretoggle.event;

import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.web.correlation.CorrelationId;

public record FeatureToggleViewRebuildRequested(
        FeatureToggleId featureToggleId,
        CorrelationId correlationId
) {
}
