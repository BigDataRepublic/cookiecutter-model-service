package nl.bigdatarepublic.streaming.embedded.adapter.zookeeper

import java.io.{File, IOException}
import java.lang.reflect.Method
import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import nl.bigdatarepublic.streaming.embedded.MapToPropsImplicit._
import org.apache.commons.io.FileUtils
import org.apache.zookeeper.server.{ServerConfig, ZooKeeperServerMain}
import org.apache.zookeeper.server.quorum.QuorumPeerConfig
import scala.collection.JavaConverters._

import scala.util.{Failure, Success, Try}


class EmbeddedZookeeper(props: Map[String, String], saveState: Boolean) extends EmbeddedService with LazyLogging {


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

    if (saveState) {
      return
    }

    logger.info("Cleaning Zookeeper data dir...")
    Try(FileUtils.cleanDirectory(new File(serverConfig.getDataDir))) match {

      case Success(_) => logger.info("Cleaned embedded Zookeeper data dir...")
      case Failure(e) => logger.error("Failed to clean embedded ZooKeeper data dir...", e)
    }
  }
}

object EmbeddedZookeeper {
  def apply(props: Map[String, String], saveState: Boolean): EmbeddedZookeeper = new EmbeddedZookeeper(props, saveState)
  def apply(props: Map[String, String]): EmbeddedZookeeper = new EmbeddedZookeeper(props, false)

  // Java compatibility.
  def apply(props: Properties, saveState: Boolean): EmbeddedZookeeper = new EmbeddedZookeeper(props.asScala.toMap, saveState)
  def apply(props: Properties): EmbeddedZookeeper = new EmbeddedZookeeper(props.asScala.toMap, false)

}