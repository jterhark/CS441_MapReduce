name := "hw2"

version := "0.1"

scalaVersion := "2.12.8"

//mainClass in (Compile, packageBin) := Some("WordCount")

libraryDependencies +="com.typesafe" % "config" % "1.3.3"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.1"

libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14"))}

libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-jobclient" % "3.2.0" % "provided"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.2.0"
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"

assemblyJarName in assembly := "CsvGrapher.jar"