package nl.bigdatarepublic.streaming.embedded.app

import java.io.File
import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import nl.bigdatarepublic.streaming.embedded.adapter.kafka.EmbeddedKafka
import nl.bigdatarepublic.streaming.embedded.adapter.kafka.connect.EmbeddedKafkaConnect
import nl.bigdatarepublic.streaming.embedded.adapter.kafka.registry.EmbeddedKafkaSchemaRegistry
import nl.bigdatarepublic.streaming.embedded.adapter.redis.EmbeddedRedis
import nl.bigdatarepublic.streaming.embedded.adapter.zookeeper.EmbeddedZookeeper
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import nl.bigdatarepublic.streaming.embedded.LogFutureImplicit._
import nl.bigdatarepublic.streaming.embedded.ConfigToMapImplicit._
import org.apache.kafka.common.utils.Utils
import org.rogach.scallop.ScallopConf
import net.ceedubs.ficus.Ficus._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

class App(services: List[EmbeddedService]) {

  def start(): Unit = services.foreach(_.start())

  def stop(): Unit = services.reverse.foreach(_.stop())
}


/**
  * CLI app for running all embedded services. in one go.
  */
object App extends LazyLogging {

  val config: Config = ConfigFactory.load()

  def main(args: Array[String]): Unit = {

    object CliOpts extends ScallopConf(args.toList) {
      val redis = opt[Boolean](descr = "Whether or not to start Redis.")
      val kafka = opt[Boolean](descr = "Whether or not to start Kafka (requires --zookeeper).")
      val zookeeper = opt[Boolean](descr = "Whether or not to start Zookeeper.")
      val connect = opt[Boolean](descr = "Whether or not to start Kafka Connect (requires --registry and --connectors).")
      val registry = opt[Boolean](descr = "Whether or not to start Kafka Schema Registry.")
      val connectors = opt[List[String]](descr = "Paths to property files for Kafka Connect connectors.")
      val persist = opt[Boolean](default = Some(false), descr = "Whether or not to persist data the services create.")
      dependsOnAll(kafka, List(zookeeper))
      dependsOnAll(registry, List(kafka, zookeeper))
      dependsOnAll(connectors, List(connect))
      dependsOnAll(connect, List(connectors, registry))
      verify()
    }

    // Construct list of services to start.
    val services = ListBuffer.empty[EmbeddedService]

    if (CliOpts.redis.supplied) {
      services += EmbeddedRedis(config.getConfig("redis").toStringMap)
      logger.info("Redis option detected, adding to services to start...")
    }

    if (CliOpts.zookeeper.supplied) {
      services += EmbeddedZookeeper(config.getConfig("zookeeper").toStringMap, CliOpts.persist.supplied)
      logger.info(s"Zookeeper option detected, adding to services to start on port (${config.getString("zookeeper.clientPort")})...")
    }


    if (CliOpts.kafka.supplied) {
      services += EmbeddedKafka(config.getConfig("kafka").toStringMap, CliOpts.persist.supplied)
      logger.info(s"Kafka option detected, adding to services to start on port (${config.getString("kafka.listeners")})...")
    }

    if (CliOpts.registry.supplied) {
      services += EmbeddedKafkaSchemaRegistry(config.getConfig("kafka.registry").toStringMap, CliOpts.persist.supplied)
      logger.info(s"Kafka Registry option detected, adding to services to start on port (${config.getString("kafka.registry.port")})...")
    }


    if (CliOpts.connect.supplied) {
      logger.info(s"Kafka Connect option detected, adding to services to start on port (${config.getString("kafka.connect.rest.port")})...")
      val futures = Future.sequence(CliOpts.connectors.toOption.get.map({ x =>
        Future {
          ConfigFactory.parseFile(new File(x)).toStringMap
        }
      })).logFailure(e => {
        logger.error("At least one of the connector property files provided cannot be loaded.", e)
      }
      ).map(
        connectors => {
          services += EmbeddedKafkaConnect(config.getConfig("kafka.connect").toStringMap, connectors, CliOpts.persist.supplied)
        }).logFailure(e => {
        logger.error("Failed to initialize Kafka Connect", e)
      }
      )
      Await.result(futures, Duration.Inf)
    }

    logger.debug(s"Services to be started: $services")

    // Construct app with supplied services, and start them.
    val app = new App(services.toList)
    app.start()
    sys.addShutdownHook(app.stop())
  }
}
