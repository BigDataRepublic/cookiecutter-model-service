package nl.bigdatarepublic.streaming.embedded.adapter.zookeeper

import java.lang.reflect.Method
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import nl.bigdatarepublic.streaming.embedded.adapter.zookeeper.MapToPropsImplicit._
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import org.apache.zookeeper.server.quorum.QuorumPeerConfig
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}

import scala.collection.JavaConverters._
import scala.reflect.io.Path
import scala.util.{Failure, Success, Try}


class EmbeddedZookeeper(props: Map[String, String], clearState: Boolean) extends EmbeddedService with LazyLogging {


  val zookeeperServer = new ZooKeeperServerMain
  val serverConfig = new ServerConfig
  val quorumPeerConfig = new QuorumPeerConfig

  // Initialization
  quorumPeerConfig.parseProperties(props.toProps)
  serverConfig.readFrom(quorumPeerConfig)
  val zookeeperThread = new Thread() {
    override def run() {
      Try(zookeeperServer.runFromConfig(serverConfig)) recover {
        case e => logger.error("Zookeeper failed", e)
      }
    }
  }

  def start() {
    if (clearState) {
      logger.info("Cleaning Zookeeper data dir before start...")
      Try(Path(serverConfig.getDataDir).deleteRecursively()) match {

        case Success(_) => logger.info("Cleaned embedded Zookeeper data dir...")
        case Failure(e) => logger.warn("Failed to clean embedded ZooKeeper data dir...", e)
      }
    }
    logger.info("Starting embedded ZooKeeper...")
    zookeeperThread.start()
  }

  def stop() {
    logger.info("Stopping embedded ZooKeeper...")
    Try({
      val shutdown: Method = classOf[ZooKeeperServerMain].getDeclaredMethod("shutdown")
      shutdown.setAccessible(true)
      shutdown.invoke(zookeeperServer)
      zookeeperThread.join()
    }
    ) match {
      case Success(_) => logger.info("Stopped embedded Zookeeper")
      case Failure(e) => logger.error("Failed to stop embedded ZooKeeper...", e)
    }

  }
}

object EmbeddedZookeeper {
  def apply(props: Map[String, String], clearState: Boolean): EmbeddedZookeeper = new EmbeddedZookeeper(props, clearState)
  def apply(props: Map[String, String]): EmbeddedZookeeper = new EmbeddedZookeeper(props, false)

  // Java compatibility.
  def apply(props: Properties, clearState: Boolean): EmbeddedZookeeper = new EmbeddedZookeeper(props.asScala.toMap, clearState)
  def apply(props: Properties): EmbeddedZookeeper = new EmbeddedZookeeper(props.asScala.toMap, false)

}