# quarkus-micrometer-extension

This is a quarkus extension that performs build time initialization, configuration, and injection of MeterRegistry, MeterBinder, and MeterFilter isntances for micrometer. This is not yet a comprehensive implementation. Support for registry implementations will be incremental, starting with Prometheus and JMX (JVM-mode only).

Most things should "just work" after enabling the extension. You'll need to: 

1. Include the quarkus-micrometer-extension dependency (and related quarkus version)
2. Include micrometer-core if you are using micrometer APIs
3. Include the micromter registry of your choice (e.g. micrometer-registry-prometheus)


## Using a SNAPSHOT

SNAPSHOT releases use JitPack:

```xml
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <properties>
    <!-- Experimental branch is built against the master branch of quarkus (999-SNAPSHOT) -->
    <quarkus-micrometer-extension.version>1.0.0-SNAPSHOT</quarkus-micrometer-extension.version>
    <micrometer.version>1.5.0</micrometer.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>dev.ebullient.quarkus-micrometer-extension</groupId>
      <artifactId>quarkus-micrometer</artifactId>
      <version>${quarkus-micrometer-extension.version}</version>
    </dependency>

    <dependency>
      <groupId>io.micrometer</groupId>
      <artifactId>micrometer-core</artifactId>
      <version>${micrometer.version}</version>
    </dependency>

    <dependency>
      <groupId>io.micrometer</groupId>
      <artifactId>micrometer-registry-prometheus</artifactId>
      <version>${micrometer.version}</version>
    </dependency>
  </dependencies>
```

Jitpack builds maven modules from source at first request, so a fetch may take a bit if the snapshot has been updated and you're the first to try grabbing it.

## Config notes (not organized, shown with defaults)

Is the micrometer extension enabled (default=true)?

```properties
quarkus.micrometer.enabled=true
```

Should registries discovered on the classpath be enabled by default (default=true)?

```properties
quarkus.micrometer.registry-enabled-default=true
```

Each registry has its own optional attribute to determine whether or not support is enabled. These optional attributes work in tandem with the global default to change discovery behavior. 

* If the registry class is found on the classpath
  * If the Micrometer Metrics extension (as a whole) is enabled
    * If `quarkus.micrometer.exporter.*.enabled` is true, the registry is enabled
    * If `quarkus.micrometer.exporter.*.enabled` is false, the registry is disabled
    * If `quarkus.micrometer.exporter.*.enabled` is unset AND `quarkus.micrometer.registry-enabled-default` is true, then the registry is enabled.
    
In most cases, you'll only have one registry on the classpath, so none of this will matter and it will all just work.   

### Prometheus support

To disable the prometheus registry:

```properties
quarkus.micrometer.exporter.prometheus.enabled=false
```

### Using Stackdriver

Stackdriver does not work in native mode, See: https://github.com/grpc/grpc-java/issues/5460.

To disable StackDriver support: 

```properties
quarkus.micrometer.exporter.stackdriver.enabled=false
```

Set the StackDriver project id: 

```properties
quarkus.micrometer.exporter.stackdriver.project-id=MY_PROJECT_ID
```

To prevent StackDriver metrics from being published in some environments (while leaving stackdriver support as a whole enabled), use: 

```properties
quarkus.micrometer.exporter.stackdriver.publish=false
```

### Using Datadog

Datadog configuration is structured in the same way that Stackdriver configuration is:
 
```properties
quarkus.micrometer.exporter.datadog.enabled=false

# Define the key used to push data using the Datadog API
quarkus.micrometer.exporter.datadog.apiKey=YOUR_KEY
```
 
To prevent Datadog metrics from being published in some environments (while leaving Datadog support as a whole enabled), use: 

```properties
quarkus.micrometer.exporter.datadog.publish=false
```

