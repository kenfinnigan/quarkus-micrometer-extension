package dev.ebullient.micrometer.deployment.export;

import javax.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.ebullient.micrometer.runtime.MicrometerRecorder;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.test.QuarkusUnitTest;

public class StackdriverEnabledTest {
    static final String REGISTRY_CLASS_NAME = "io.micrometer.stackdriver.StackdriverMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withConfigurationResource("test-logging.properties")
            .overrideConfigKey("quarkus.micrometer.export.stackdriver.enabled", "true")
            .overrideConfigKey("quarkus.micrometer.export.stackdriver.publish", "false")
            .overrideConfigKey("quarkus.micrometer.export.stackdriver.project-id", "myproject")
            .overrideConfigKey("quarkus.micrometer.registry-enabled-default", "false")
            .overrideConfigKey("quarkus.micrometer.binder-enabled-default", "false")
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(StackdriverRegistryProcessor.REGISTRY_CLASS));

    @Inject
    MeterRegistry registry;

    @Test
    public void testMeterRegistryPresent() {
        // Stackdriver is enabled (alone, all others disabled)
        Assertions.assertNotNull(registry, "A registry should be configured");
        Assertions.assertTrue(REGISTRY_CLASS.equals(registry.getClass()), "Should be StackdriverMeterRegistry");
    }
}
