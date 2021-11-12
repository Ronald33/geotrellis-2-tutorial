name := "geotrellis-2-tutorial"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies += "org.locationtech.geotrellis" %% "geotrellis-spark" % "2.2.0"
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.1"
libraryDependencies +=  "com.typesafe.akka" %% "akka-actor"  % "2.4.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.3"