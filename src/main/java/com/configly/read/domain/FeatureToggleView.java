package com.configly.read.domain;

import com.configly.contracts.event.featuretoggle.FeatureToggleCreated;
import com.configly.contracts.event.featuretoggle.FeatureToggleStatusChanged;
import com.configly.contracts.event.featuretoggle.FeatureToggleUpdated;
import com.configly.contracts.event.featuretoggle.FeatureToggleValueChanged;
import com.configly.model.CreatedAt;
import com.configly.model.Revision;
import com.configly.model.UpdatedAt;
import com.configly.model.environment.EnvironmentId;
import com.configly.model.featuretoggle.FeatureToggleDescription;
import com.configly.model.featuretoggle.FeatureToggleId;
import com.configly.model.featuretoggle.FeatureToggleName;
import com.configly.model.featuretoggle.FeatureToggleStatus;
import com.configly.value.FeatureToggleValue;
import com.configly.value.FeatureToggleValueBuilder;
import com.configly.value.FeatureToggleValueSnapshot;

public record FeatureToggleView(
        FeatureToggleId id,
        EnvironmentId environmentId,
        FeatureToggleName name,
        FeatureToggleDescription description,
        FeatureToggleValue value,
        FeatureToggleStatus status,
        Revision revision,
        CreatedAt createdAt,
        UpdatedAt updatedAt,
        boolean consistent
) {


    public static FeatureToggleView create(FeatureToggleCreated event) {
        return new FeatureToggleView(
                FeatureToggleId.create(event.id()),
                EnvironmentId.create(event.environmentId()),
                FeatureToggleName.create(event.name()),
                FeatureToggleDescription.create(event.description()),
                FeatureToggleValueBuilder.from(FeatureToggleValueSnapshot.of(event.value()), event.type()),
                FeatureToggleStatus.valueOf(event.status()),
                Revision.from(event.revision()),
                CreatedAt.of(event.createdAt()),
                UpdatedAt.of(event.updatedAt()),
                true
        );
    }

    public FeatureToggleView apply(FeatureToggleUpdated event) {
        return new FeatureToggleView(
                this.id,
                this.environmentId,
                FeatureToggleName.create(event.name()),
                FeatureToggleDescription.create(event.description()),
                this.value,
                this.status,
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }

    public FeatureToggleView apply(FeatureToggleValueChanged event) {
        return new FeatureToggleView(
                this.id,
                this.environmentId,
                this.name,
                this.description,
                FeatureToggleValueBuilder.from(FeatureToggleValueSnapshot.of(event.value()), event.type()),
                this.status,
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }

    public FeatureToggleView apply(FeatureToggleStatusChanged event) {
        return new FeatureToggleView(
                this.id,
                this.environmentId,
                this.name,
                this.description,
                this.value,
                FeatureToggleStatus.valueOf(event.status()),
                Revision.from(event.revision()),
                this.createdAt,
                UpdatedAt.of(event.updatedAt()),
                this.consistent
        );
    }
}
