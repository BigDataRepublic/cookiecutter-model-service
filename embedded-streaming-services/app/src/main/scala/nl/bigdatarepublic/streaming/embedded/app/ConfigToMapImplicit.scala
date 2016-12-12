package nl.bigdatarepublic.streaming.embedded.app

import com.typesafe.config.Config

/**
  * Created by stefan.vanwouw on 28-11-2016.
  */
object ConfigToMapImplicit {

  implicit class StringMapFromConfig(val config: Config) extends AnyVal {

    def toStringMap: Map[String, String] = {
      import scala.collection.JavaConverters._
      config.entrySet().asScala.map { entry =>
        entry.getKey.replace("\"", "") -> entry.getValue.unwrapped().toString // Replace " and unwrap to support non-compatible keys such as x=1 , "x.subkey"=2
      }.toMap
    }
    def toMap: Map[String, AnyRef] = {
      import scala.collection.JavaConverters._
      config.entrySet().asScala.map { entry =>
        entry.getKey.replace("\"", "") -> entry.getValue.unwrapped()
      }.toMap
    }
  }

}
