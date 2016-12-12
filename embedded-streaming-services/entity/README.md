# Embedded Streaming Entity module

Common components and interfaces go here.

## How to implement your own embedded service

1. Create a new sbt/maven module preferably with the [service]-adapter naming convention.
2. Implement the [EmbeddedService](src/main/scala/nl/bigdatarepublic/streaming/embedded/entity/EmbeddedService.scala) interface for your service.
3. (Optional) Add your service to the all-in-one [App](../app/src/main/scala/nl/bigdatarepublic/streaming/embedded/app/App.scala)
