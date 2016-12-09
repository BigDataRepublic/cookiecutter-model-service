package nl.bigdatarepublic.streaming.embedded.adapter.kafka.registry

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import io.confluent.kafka.schemaregistry.rest.{SchemaRegistryConfig, SchemaRegistryRestApplication}
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import org.eclipse.jetty.server.Server

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

class EmbeddedKafkaSchemaRegistry(props: Map[String, String]) extends EmbeddedService with LazyLogging {


  val app = new SchemaRegistryRestApplication(new SchemaRegistryConfig(props.asJava))
  var server : Server = _

  def start(): Unit = {

    Try({
      logger.info("Starting embedded KafkaSchemaRegistry...")
      // Only create server here instead of constructor, because it is initializing stuff already.
      server = app.createServer()
      server.start()
    }) match {
      case Success(_) => logger.info("Successfully started KafkaSchemaRegistry")
      case Failure(e) => logger.error("Failed to start KafkaSchemaRegistry", e)
    }
  }

  def stop(): Unit = {
    Try({
      logger.info("Stopping embedded KafkaSchemaRegistry...")
      // Only create server here instead of constructor, because it is initializing stuff already.
      server.stop()
    }) match {
      case Success(_) => logger.info("Successfully stopped KafkaSchemaRegistry")
      case Failure(e) => logger.error("Failed to stop KafkaSchemaRegistry", e)
    }


  }

}

object EmbeddedKafkaSchemaRegistry {

  def apply(props: Map[String, String]): EmbeddedKafkaSchemaRegistry = new EmbeddedKafkaSchemaRegistry(props)

  // Java compatibility
  def apply(props: Properties): EmbeddedKafkaSchemaRegistry = new EmbeddedKafkaSchemaRegistry(props.asScala.toMap)
}
