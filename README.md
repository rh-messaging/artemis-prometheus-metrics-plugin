# Artemis Prometheus Metrics Plugin

This is a metrics plugin implementation for the ActiveMQ Artemis message broker.
It provides integration with Prometheus using two modules:

- **artemis-prometheus-metrics-plugin** Provides the actual implementation of
  `org.apache.activemq.artemis.core.server.metrics.ActiveMQMetricsPlugin` and
  packages it with the micrometer and Prometheus dependencies in an "uber" jar.

- **artemis-prometheus-metrics-plugin-servlet** Provides a war file containing
  a simple servlet which can be deployed to the broker's embedded web server
  which then Prometheus can use to scrape metrics.

## Building

Simply run `mvn install`. This command will build both modules and the output
will be in their respective `target` directories.

## Installing in ActiveMQ Artemis

After building the artifacts follow these steps:

1. Copy `artemis-prometheus-metrics-plugin/target/artemis-prometheus-metrics-plugin-<VERSION>.jar`
   to `<ARTEMIS_INSTANCE>/lib`.

1. Add this to your `<ARTEMIS_INSTANCE>/etc/broker.xml`:

       <metrics><plugin class-name="com.redhat.amq.broker.core.server.metrics.plugins.ArtemisPrometheusMetricsPlugin"/></metrics>

1. Create the directory `<ARTEMIS_INSTANCE>/web`.

1. Copy `artemis-prometheus-metrics-plugin-servlet/target/metrics.war` to `<ARTEMIS_INSTANCE>/web`.

1. Add this to the `web` element in `<ARTEMIS_INSTANCE>/etc/bootstrap.xml`:

       <app url="metrics" war="metrics.war"/>
