package dev.ebullient.micrometer.deployment.export;

import java.util.function.BooleanSupplier;

import org.jboss.logging.Logger;

import dev.ebullient.micrometer.deployment.MicrometerRegistryProviderBuildItem;
import dev.ebullient.micrometer.runtime.MicrometerRecorder;
import dev.ebullient.micrometer.runtime.config.MicrometerConfig;
import dev.ebullient.micrometer.runtime.export.JmxMeterRegistryProvider;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.pkg.steps.NativeBuild;

/**
 * Add support for the Jmx Meter Registry. Note that the registry may not
 * be available at deployment time for some projects: Avoid direct class
 * references.
 */
public class JmxRegistryProcessor {
    private static final Logger log = Logger.getLogger(JmxRegistryProcessor.class);

    static final String REGISTRY_CLASS_NAME = "io.micrometer.jmx.JmxMeterRegistry";
    static final Class<?> REGISTRY_CLASS = MicrometerRecorder.getClassForName(REGISTRY_CLASS_NAME);

    static class JmxEnabled implements BooleanSupplier {
        MicrometerConfig mConfig;

        public boolean getAsBoolean() {
            return REGISTRY_CLASS != null && mConfig.checkRegistryEnabledWithDefault(mConfig.export.jmx);
        }
    }

    @BuildStep(onlyIf = { NativeBuild.class, JmxEnabled.class })
    MicrometerRegistryProviderBuildItem createJmxRegistry(CombinedIndexBuildItem index) {
        log.info("JMX Meter Registry does not support running in native mode.");
        return null;
    }

    /** Jmx does not work with GraalVM */
    @BuildStep(onlyIf = JmxEnabled.class, onlyIfNot = NativeBuild.class, loadsApplicationClasses = true)
    MicrometerRegistryProviderBuildItem createJmxRegistry(CombinedIndexBuildItem index,
            BuildProducer<AdditionalBeanBuildItem> additionalBeans) {

        // Add the Jmx Registry Producer
        additionalBeans.produce(AdditionalBeanBuildItem.builder()
                .addBeanClass(JmxMeterRegistryProvider.class)
                .setUnremovable().build());

        // Include the JmxMeterRegistry in a possible CompositeMeterRegistry
        return new MicrometerRegistryProviderBuildItem(REGISTRY_CLASS);
    }
}
