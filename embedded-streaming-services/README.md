# Embedded Streaming Services

This repository provides both a standalone app as well as unit test components for testing various streaming related Big data services.

This is useful for unit testing code without having to deploy to a VM or other more complicated test environment.

Current supported services:

* Redis 2.8.19
* Kafka, Kafka Connect, Kafka Schema Registry 0.9.0.1 (Confluent patches included).
* Zookeeper 3.4.6


Please check the releases tab for available app binaries. 
All individual components are also available at our Bintray repository, so you can use them in the unit tests of your project.

Each of the components has their own readme:

* [app](app/README.md) - All-in-one commandline app
* [entity](entity/README.md) - Includes common code used by other components such as the EmbeddedService interface. Check here on how to implement your own service.
* [kafka-adapter](kafka-adapter/README.md) - Includes embedded versions of Kafka, Kafka Connect, and Kafka Schema Registry.
* [zookeeper-adapter](zookeeper-adapter/README.md) - Includes an embedded version of Zookeeper.
* [redis-adapter](redis-adapter/README.md) - Includes an embedded version of Redis.

... more to be added, feel free to create a PR ... 



