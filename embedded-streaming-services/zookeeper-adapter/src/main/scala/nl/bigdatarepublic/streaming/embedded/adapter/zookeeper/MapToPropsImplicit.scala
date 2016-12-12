package nl.bigdatarepublic.streaming.embedded.adapter.zookeeper

import java.util.Properties

/**
  * Created by stefan.vanwouw on 28-11-2016.
  */
object MapToPropsImplicit {

  implicit class PropertiesFromMap(val props: Map[String, String]) extends AnyVal {

    def toProps: Properties =
      (new Properties /: props) {
        case (a, (k, v)) =>
          a.put(k, v)
          a
      }
  }

}
