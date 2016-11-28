import sbt._
import sbt.Keys._


val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val scalaMockTest = "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
val logbackTest = "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"
val scalaConfig = "com.iheart" %% "ficus" % "1.3.2" // Scala wrapper for Config.
val scallop = "org.rogach" %% "scallop" % "2.0.5"

val kafkaVersion = "0.9.0.0"
val confluentVersion = "2.0.1"
val hiveVersion = "1.2.1"
val hadoopVersion = "2.7.1"


// Needed for all versions
val kafkaConnectApi = "org.apache.kafka" % "connect-api" % kafkaVersion
val kafkaConnectFile = "org.apache.kafka" % "connect-file" % kafkaVersion
val kafkaConnectJson = "org.apache.kafka" % "connect-json" % kafkaVersion
val kafkaConnectRuntime = "org.apache.kafka" % "connect-runtime" % kafkaVersion
val kafkaConnectJdbc = "io.confluent" % "kafka-connect-jdbc" % confluentVersion exclude("org.slf4j", "slf4j-log4j12")
val kafkaConnectAvroConverter = "io.confluent" % "kafka-connect-avro-converter" % confluentVersion exclude("org.slf4j", "slf4j-log4j12")
val hiveJdbc = "org.apache.hive" % "hive-jdbc" % hiveVersion exclude("org.slf4j", "slf4j-log4j12") exclude("org.apache.logging.log4j", "log4j-slf4j-impl") exclude("org.eclipse.jetty.aggregate", "jetty-all") exclude("javax.ws.rs", "jsr311-api")
val hadoopClient = "org.apache.hadoop" % "hadoop-client" % hadoopVersion exclude("org.slf4j", "slf4j-log4j12") exclude("javax.servlet", "servlet-api") exclude("javax.ws.rs", "jsr311-api")
val kafka = "org.apache.kafka" %% "kafka" % kafkaVersion exclude("org.slf4j", "slf4j-log4j12")
val embeddedRedis = "com.github.kstyrc" % "embedded-redis" % "0.6"
val kafkaSchemaRegistry = "io.confluent" % "kafka-schema-registry" % confluentVersion exclude("org.slf4j", "slf4j-log4j12")

lazy val commonSettings = Seq(
  resolvers ++= Seq( //Needs to be in commonsettings to be applied to project
    "Artima Maven Repository" at "http://repo.artima.com/releases", // Scala test
    Resolver.typesafeRepo("releases"),
    Resolver.bintrayRepo("iheartradio","maven"),
    Resolver.sonatypeRepo("releases"),
    "Confluent.io Maven Repository" at "http://packages.confluent.io/maven"
  ),
  organization := "nl.bigdatarepublic",
  version := "0.1.0-SNAPSHOT",
  crossPaths := false,
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    logbackTest

  )
)

lazy val embeddedStreamingServicesApp = Project(id = "app", base = file("app")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-app",
    libraryDependencies ++= Seq(
      embeddedRedis,
      kafka,
      kafkaConnectApi,
      kafkaConnectFile,
      kafkaSchemaRegistry,
      kafkaConnectJdbc,
      kafkaConnectAvroConverter,
      hiveJdbc,
      hadoopClient,
      kafkaConnectRuntime,
      kafkaConnectJson,
      scallop,
      scalaLogging,
      scalaConfig,
      logback
    )
  )