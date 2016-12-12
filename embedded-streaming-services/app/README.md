# Embedded Streaming Services standalone app

This module packages all the available embedded services into a commandline utility.

## Usage

Run the jar with the following command to check all available options:
```
java -jar embedded-streaming-services-<version>.jar --help
``` 

Current options are:

```
  -c, --connect                Whether or not to start Kafka Connect (requires
                               --registry and --connectors).
      --connectors  <arg>...   Paths to property files for Kafka Connect
                               connectors.
  -k, --kafka                  Whether or not to start Kafka (requires
                               --zookeeper).
  -p, --persist                Whether or not to persist the data the services
                               create. If not set, data dirs are cleared out
                               when starting.
  -r, --redis                  Whether or not to start Redis.
      --registry               Whether or not to start Kafka Schema Registry.
  -z, --zookeeper              Whether or not to start Zookeeper.
      --help                   Show help message
```

Example call:

```
java -jar embedded-streaming-services-<version>.jar --kafka --zookeeper --registry --connect --connectors app/src/main/resources/kafka-connect-connector-example.conf --redis
```

This tries to read a test.txt file in your current working directory. Every time you write a line, it will send this line as Avro string to the connect-test topic in the embedded Kafka.

For this to work, an Avro schema is registered at the embedded Kafka Schema Registry. You can check the schema at http://localhost:9080/subjects/connect-test-value/latest (note that this is a very simple schema; other connectors can generate more complex schemas).

You can check the contents of the connect-test Kafka topic using the Kafka Avro console consumer ([Confluent 2.0.1](http://packages.confluent.io/archive/2.0/confluent-2.0.1-2.11.7.zip) download required, or test from within the IDE having this project checked out from GitHub, see below):

```
confluent-2.0.1/bin/kafka-avro-console-consumer --topic connect-test --zookeeper localhost:2181 --from-beginning --property schema.registry.url=http://localhost:9080
```

Or provide this to kafka.tools.ConsoleConsumer directly if you run it from within your IDE (no confluent download required):
```
--topic connect-test --zookeeper localhost:2181 --formatter io.confluent.kafka.formatter.AvroMessageFormatter --property schema.registry.url=http://localhost:9080 --from-beginning
```

To check if Redis is running as expected. You should be able to telnet to localhost 6379 and be able to execute redis commands (as per https://redis.io/commands ).

## Connector configuration options

The above example illustrates a very simple example which monitors a file line by line and uses a trivial schema (just a String). Other connectors may generate more complex schemas, and connect to various sources.

You can find all connectors available [here](https://www.confluent.io/product/connectors/) and [here](http://docs.confluent.io/2.0.0/connect/connectors.html).

## Common issues

1. When Redis does not want to start, please check if the previous instance was terminated correctly. If not, manually terminate the redis-server-2.8.19 executable in the task manager.
