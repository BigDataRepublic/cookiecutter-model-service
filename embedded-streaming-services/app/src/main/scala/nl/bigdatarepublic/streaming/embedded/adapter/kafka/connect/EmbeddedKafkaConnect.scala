package nl.bigdatarepublic.streaming.embedded.adapter.kafka.connect

import java.io.File
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import nl.bigdatarepublic.streaming.embedded.LogFutureImplicit._
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import org.apache.commons.io.FileUtils
import org.apache.kafka.connect.runtime.rest.RestServer
import org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo
import org.apache.kafka.connect.runtime.standalone.{StandaloneConfig, StandaloneHerder}
import org.apache.kafka.connect.runtime.{Connect, ConnectorConfig, Herder, Worker}
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore
import org.apache.kafka.connect.util.FutureCallback

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success, Try}


/**
  * Embedded kafka connect standalone.
  */
class EmbeddedKafkaConnect(props: Map[String, String], connectorProps: List[Map[String,String]], clearState: Boolean) extends EmbeddedService with LazyLogging {


  val config = new StandaloneConfig(props.asJava)
  val worker = new Worker(config, new MemoryOffsetBackingStore)
  val restServer = new RestServer(config)
  val herder = new StandaloneHerder(worker)
  val connect = new Connect(worker, herder, restServer)

  implicit class JavaPromiseFuture[T](p: Promise[T]) {
    def asJava: FutureCallback[T] =
      new FutureCallback[T] { (error: Throwable, info: T) => {
        if (error != null) {
          p failure error
        } else {
          p success info
        }
      }
      }
  }

    def start(): Unit = {
      if (clearState) {
        logger.info("Cleaning Kafka Connect storage dir before start...")
        Try {
          FileUtils.cleanDirectory(new File(props("offset.storage.file.filename")).getParentFile)
        } match {
          case Success(_) => logger.info("Successfully cleaned Kafka Connect data dir.")
          case Failure(e) => logger.error("Failed to clean Kafka Connect data dir.")
        }
      }
      connect.start()


      // Add all connectors in parallel.
      val futures = connectorProps.map { prop =>

        val p = Promise[Herder.Created[ConnectorInfo]]

        herder.putConnectorConfig(prop(ConnectorConfig.NAME_CONFIG), prop.asJava, false, p.asJava)
        p.future
      }

      // Create a future of all futures to be able to fail fast if at least one of the connector futures fails.
      val future = Future.sequence(futures).logFailure {e => {
        logger.error("At least one connector could not be added to Kafka Connect, terminating...", e)
      }}.logSuccess { _ => {
        logger.info("All connectors were successfully added.")
      }}
//      }} recover {
//        case _ => connect.stop()
//      }


      Await.ready(future, Duration.Inf)
      connect.start()

      logger.info("Successfully started Kafka Connect")
    }

  def stop(): Unit = {
    connect.stop()
  }

}

object EmbeddedKafkaConnect {
  def apply(props: Map[String, String], connectorProps: List[Map[String, String]], clearState: Boolean): EmbeddedKafkaConnect = new EmbeddedKafkaConnect(props, connectorProps, clearState)
  def apply(props: Map[String, String], connectorProps: List[Map[String, String]]): EmbeddedKafkaConnect = new EmbeddedKafkaConnect(props, connectorProps, false)

  // Java compatibility.
  def apply(props: Properties, connectorProps: java.util.List[Properties], clearState: Boolean): EmbeddedKafkaConnect = new EmbeddedKafkaConnect(props.asScala.toMap, connectorProps.asScala.map(_.asScala.toMap).toList, clearState)
  def apply(props: Properties, connectorProps: java.util.List[Properties]): EmbeddedKafkaConnect = new EmbeddedKafkaConnect(props.asScala.toMap, connectorProps.asScala.map(_.asScala.toMap).toList, false)
}
