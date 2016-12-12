import sbt._
import sbt.Keys._

publish := { } // Do not try to publish root project to bintray.
bintrayRelease := { } // Do not publish root project to bintray.

val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
val scalaMockTest = "org.scalamock" %% "scalamock-scalatest-support" % "3.2.2" % "test"
val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
val logbackTest = "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
val logback = "ch.qos.logback" % "logback-classic" % "1.1.7"
val scalaConfig = "com.iheart" %% "ficus" % "1.3.2" // Scala wrapper for Config.
val scallop = "org.rogach" %% "scallop" % "2.0.5"
val slf4jOverlog4j = "org.slf4j" % "log4j-over-slf4j" % "1.7.6"

val kafkaVersion = "0.9.0.1-cp1"
val confluentVersion = "2.0.1"


// Needed for all versions
val kafkaConnectApi = "org.apache.kafka" % "connect-api" % kafkaVersion
val kafkaConnectFile = "org.apache.kafka" % "connect-file" % kafkaVersion
val kafkaConnectJson = "org.apache.kafka" % "connect-json" % kafkaVersion
val kafkaConnectRuntime = "org.apache.kafka" % "connect-runtime" % kafkaVersion
val kafkaConnectJdbc = "io.confluent" % "kafka-connect-jdbc" % confluentVersion exclude("org.slf4j", "slf4j-log4j12")
val kafkaConnectAvroConverter = "io.confluent" % "kafka-connect-avro-converter" % confluentVersion exclude("org.slf4j", "slf4j-log4j12")
val kafka = "org.apache.kafka" %% "kafka" % kafkaVersion exclude("org.slf4j", "slf4j-log4j12") exclude("log4j", "log4j")
val zookeeper = "org.apache.zookeeper" % "zookeeper" % "3.4.6" // version should be in sync with kafka dependencies.
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
  version := "0.1.0",
  crossPaths := false,
  scalaVersion := "2.11.8",
  libraryDependencies ++= Seq(
    logbackTest

  ),
  assemblyMergeStrategy in assembly := {
    case PathList("javax", "inject", xs @ _*)         => MergeStrategy.first
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  bintrayReleaseOnPublish in ThisBuild := false,
  licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
  publishMavenStyle := true,
  bintrayOrganization := Some("bigdatarepublic")
)

lazy val embeddedStreamingEntity = Project(id = "entity", base = file("entity")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-entity"
  )

lazy val embeddedStreamingRedisAdapter = Project(id = "redis-adapter", base = file("redis-adapter")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-redis-adapter",
    libraryDependencies ++= Seq(
      embeddedRedis,
      scalaLogging
    )
  ).dependsOn(embeddedStreamingEntity)

lazy val embeddedStreamingZookeeperAdapter = Project(id = "zookeeper-adapter", base = file("zookeeper-adapter")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-zookeeper-adapter",
    libraryDependencies ++= Seq(
      zookeeper,
      scalaLogging
    )
  ).dependsOn(embeddedStreamingEntity)

lazy val embeddedStreamingKafkaAdapter = Project(id = "kafka-adapter", base = file("kafka-adapter")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-kafka-adapter",
    libraryDependencies ++= Seq(
      kafka,
      kafkaConnectApi,
      kafkaConnectFile,
      kafkaSchemaRegistry,
      kafkaConnectJdbc,
      kafkaConnectAvroConverter,
      kafkaConnectRuntime,
      kafkaConnectJson,
      scalaLogging
    )
  ).dependsOn(embeddedStreamingEntity)

lazy val embeddedStreamingServicesApp = Project(id = "app", base = file("app")).
  settings(commonSettings: _*).
  settings(
    name := "embedded-streaming-services-app",
    libraryDependencies ++= Seq(
      kafka,
      kafkaConnectApi,
      kafkaConnectFile,
      kafkaSchemaRegistry,
      kafkaConnectJdbc,
      kafkaConnectAvroConverter,
      kafkaConnectRuntime,
      kafkaConnectJson,
      scallop,
      scalaLogging,
      scalaConfig,
      logback,
      slf4jOverlog4j // Zookeeper logging should be captured by sl4fj
    ),
    mainClass in assembly := Some("nl.bigdatarepublic.streaming.embedded.app.App"),
    publish := { }, // Do not publish to bintray.
    bintrayRelease := { } // Do not publish to bintray.
  ).dependsOn(embeddedStreamingEntity, embeddedStreamingKafkaAdapter, embeddedStreamingRedisAdapter, embeddedStreamingZookeeperAdapter)