package dev.ebullient.micrometer.runtime.config;

import java.util.Optional;

import dev.ebullient.micrometer.runtime.config.MicrometerConfig.CapabilityEnabled;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;

@ConfigGroup
public class PrometheusConfig implements CapabilityEnabled {
    /**
     * Support for export to Prometheus.
     * <p>
     * Support for Prometheus will be enabled if micrometer
     * support is enabled, the PrometheusMeterRegistry is on the classpath
     * and either this value is true, or this value is unset and
     * {@code quarkus.micrometer.registry-enabled-default} is true.
     */
    @ConfigItem
    public Optional<Boolean> enabled;

    /**
     * The path for the prometheus endpoint. The default value is {@code /prometheus}.
     */
    @ConfigItem(defaultValue = "/prometheus")
    public String path;

    @Override
    public Optional<Boolean> getEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "{path='" + path
                + ",enabled=" + enabled
                + '}';
    }
}
