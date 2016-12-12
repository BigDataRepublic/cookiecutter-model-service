package nl.bigdatarepublic.streaming.embedded.adapter.redis

import java.util.Properties

import com.typesafe.scalalogging.LazyLogging
import nl.bigdatarepublic.streaming.embedded.entity.EmbeddedService
import redis.embedded.RedisServer

import scala.collection.JavaConverters._

/**
  * Encapsulates Redis.
  */
class EmbeddedRedis(props: Map[String,String]) extends LazyLogging with EmbeddedService {

  val redisServer : RedisServer = new RedisServer(props("port").toInt)


  def start(): Unit = {
    logger.info("Starting embedded Redis...")
    redisServer.start()
    logger.info("Successfully started embedded Redis")
  }

  def stop(): Unit =  {
    logger.info("Stopping embedded Redis...")
    redisServer.stop()
    logger.info("Successfully stopped embedded Redis")
  }

}

object EmbeddedRedis {
  def apply(props: Map[String, String]): EmbeddedRedis = new EmbeddedRedis(props)

  // Java compatibility.
  def apply(props: Properties): EmbeddedRedis = new EmbeddedRedis(props.asScala.toMap)
}
