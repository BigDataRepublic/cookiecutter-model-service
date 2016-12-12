package nl.bigdatarepublic.streaming.embedded.adapter.kafka

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import kafka.server.{KafkaConfig, KafkaServerStartable}
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService

import scala.collection.JavaConverters._
import scala.reflect.io.Path
import scala.util.{Failure, Success, Try}


class EmbeddedKafka(props: Map[String, String], clearState: Boolean) extends LazyLogging with EmbeddedService {

  val kafka: KafkaServerStartable = new KafkaServerStartable(KafkaConfig(props.asJava))

  def start(): Unit = {
    // Clear out the existing kafka dir upon startup.
    if (clearState) {
      logger.info("Cleaning Kafka data dir before start...")
      kafka.serverConfig.logDirs.foreach { x =>
        Try(Path(x).deleteRecursively()) match {
          case Success(true) => logger.info("Successfully cleaned Kafka data dir...")
          case Success(false) => logger.info("Failed to clean Kafka data dir...")
          case Failure(e) => logger.warn("Failed to clean Kafka data dir", e)
        }
      }
    }
    logger.info("Starting embedded Kafka...")
    kafka.startup()
    logger.info("Successfully started embedded Kafka")

  }

  def stop(): Unit = {
    logger.info("Stopping embedded Kafka...")
    kafka.shutdown()
    logger.info("Successfully stopped embedded Kafka")

  }

}



object EmbeddedKafka {
  def apply(props: Map[String, String], clearState: Boolean): EmbeddedKafka = new EmbeddedKafka(props, clearState)
  def apply(props: Map[String, String]): EmbeddedKafka = new EmbeddedKafka(props, false)

  // Java compatibility
  def apply(props: Properties, clearState: Boolean): EmbeddedKafka = new EmbeddedKafka(props.asScala.toMap, clearState)
  def apply(props: Properties): EmbeddedKafka = new EmbeddedKafka(props.asScala.toMap, false)
}
