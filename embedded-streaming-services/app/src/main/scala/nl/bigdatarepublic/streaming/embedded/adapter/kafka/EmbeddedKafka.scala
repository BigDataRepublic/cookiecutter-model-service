package nl.bigdatarepublic.streaming.embedded.adapter.kafka


import java.io.File
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import kafka.server.{KafkaConfig, KafkaServerStartable}
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._

import scala.util.{Failure, Success, Try}


class EmbeddedKafka(props: Map[String, String], saveState: Boolean) extends LazyLogging with EmbeddedService {

  val kafka: KafkaServerStartable = new KafkaServerStartable(KafkaConfig(props.asJava))

  def start(): Unit = {
  logger.info("Starting embedded Kafka...")
  kafka.startup()
  logger.info("Successfully started embedded Kafka")

}

  def stop(): Unit = {
    logger.info("Stopping embedded Kafka...")
    kafka.shutdown()
    logger.info("Successfully stopped embedded Kafka")

    // Return if we do not want to clean out the kafka data dir.
    if (saveState) {
      return
    }

    logger.info("Cleaning Kafka data dir...")
    kafka.serverConfig.logDirs.foreach { x =>
      Try(FileUtils.cleanDirectory(new File(x))) match {
        case Success(prop) => logger.info("Successfully cleaned Kafka data dir...")
        case Failure(e) => logger.error("Failed to clean Kafka data dir", e)
      }
    }
  }

}



object EmbeddedKafka {
  def apply(props: Map[String, String], saveState: Boolean): EmbeddedKafka = new EmbeddedKafka(props, saveState)
  def apply(props: Map[String, String]): EmbeddedKafka = new EmbeddedKafka(props, false)

  // Java compatibility
  def apply(props: Properties, saveState: Boolean): EmbeddedKafka = new EmbeddedKafka(props.asScala.toMap, saveState)
  def apply(props: Properties): EmbeddedKafka = new EmbeddedKafka(props.asScala.toMap, false)
}
