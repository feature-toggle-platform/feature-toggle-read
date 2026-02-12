package pl.feature.toggle.service.read;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pl.feature.toggle.service.event.processing.api.RevisionProjectionApplier;
import pl.feature.toggle.service.event.processing.internal.DefaultRevisionProjectionApplier;
import pl.feature.toggle.service.read.infrastructure.support.*;

public abstract class AbstractUnitTest {

    protected EnvironmentViewConsistencySpy environmentViewConsistencySpy;
    protected EnvironmentViewProjectionRepositorySpy environmentViewProjectionRepositorySpy;
    protected EnvironmentViewQueryRepositoryStub environmentViewQueryRepositoryStub;
    protected ProjectViewConsistencySpy projectViewConsistencySpy;
    protected ProjectViewProjectionRepositorySpy projectViewProjectionRepositorySpy;
    protected ProjectViewQueryRepositoryStub projectViewQueryRepositoryStub;
    protected FeatureToggleViewConsistencySpy featureToggleViewConsistencySpy;
    protected FeatureToggleViewProjectionRepositorySpy featureToggleViewProjectionRepositorySpy;
    protected FeatureToggleViewQueryRepositoryStub featureToggleViewQueryRepositoryStub;

    protected RevisionProjectionApplier revisionProjectionApplier;
    protected ApplicationEventPublishedSpy applicationEventPublishedSpy;

    @BeforeEach
    void setUp() {
        environmentViewConsistencySpy = new EnvironmentViewConsistencySpy();
        environmentViewProjectionRepositorySpy = new EnvironmentViewProjectionRepositorySpy();
        environmentViewQueryRepositoryStub = new EnvironmentViewQueryRepositoryStub();
        projectViewConsistencySpy = new ProjectViewConsistencySpy();
        projectViewProjectionRepositorySpy = new ProjectViewProjectionRepositorySpy();
        projectViewQueryRepositoryStub = new ProjectViewQueryRepositoryStub();
        featureToggleViewConsistencySpy = new FeatureToggleViewConsistencySpy();
        featureToggleViewProjectionRepositorySpy = new FeatureToggleViewProjectionRepositorySpy();
        featureToggleViewQueryRepositoryStub = new FeatureToggleViewQueryRepositoryStub();
        revisionProjectionApplier = DefaultRevisionProjectionApplier.create();
        applicationEventPublishedSpy = new ApplicationEventPublishedSpy();
    }

    @AfterEach
    void tearDown() {
        environmentViewConsistencySpy.reset();
        environmentViewProjectionRepositorySpy.reset();
        environmentViewQueryRepositoryStub.reset();
        projectViewConsistencySpy.reset();
        projectViewProjectionRepositorySpy.reset();
        projectViewQueryRepositoryStub.reset();
        featureToggleViewConsistencySpy.reset();
        featureToggleViewProjectionRepositorySpy.reset();
        featureToggleViewQueryRepositoryStub.reset();
    }


}
