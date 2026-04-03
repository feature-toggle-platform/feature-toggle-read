package com.configly.read.infrastructure.out.rest;

import lombok.AllArgsConstructor;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.web.client.InternalRestClient;
import com.configly.web.client.ServiceId;
import com.configly.read.application.port.out.FeatureToggleClient;
import com.configly.read.domain.FeatureToggleView;
import com.configly.read.infrastructure.out.rest.dto.FeatureToggleViewDto;

@AllArgsConstructor
class RestFeatureToggleClient implements FeatureToggleClient {

    private final InternalRestClient internalRestClient;

    @Override
    public FeatureToggleView fetchFeatureToggle(FeatureToggleId featureToggleId) {
        return internalRestClient.get(
                        ServiceId.WRITE,
                        "/internal/feature-toggles/{featureToggleId}/view",
                        FeatureToggleViewDto.class,
                        featureToggleId.idAsString()
                )
                .toDomain();
    }
}
