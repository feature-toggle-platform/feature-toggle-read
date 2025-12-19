package pl.feature.toggle.service.read.infrastructure.in.rest;

import pl.feature.toggle.service.model.featuretoggle.FeatureToggleId;
import pl.feature.toggle.service.read.application.port.in.FeatureToggleReadUseCase;
import pl.feature.toggle.service.read.infrastructure.in.rest.view.FeatureToggleView;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/rest/api/feature-toggles/read")
class FeatureToggleController {

    private final FeatureToggleReadUseCase featureToggleReadUseCase;

    @GetMapping("/{id}")
    FeatureToggleView getFeatureToggle(@PathVariable String id) {
        return featureToggleReadUseCase.getFeatureToggle(FeatureToggleId.create(id));
    }

    @GetMapping
    List<FeatureToggleView> getFeatureToggles(){
        return featureToggleReadUseCase.getAll();
    }

}
