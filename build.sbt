
name := "session-event-processor"
version := "1.0"
scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "spark-core",
  "spark-hive",
  "spark-sql",
).map( "org.apache.spark" %% _ % "2.4.5" )

libraryDependencies += "org.json4s" %% "json4s-native" % "3.5.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.3.0-SNAP2" % Test
libraryDependencies += "org.mockito" %% "mockito-scala-scalatest" % "1.13.7" % Test

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

resolvers += Resolver.url("bintray-sbt-plugins", url("http://dl.bintray.com/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
