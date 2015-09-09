val hadoopVer = "2.7.1"

name := "vertica-hadoop-connector"

organization := "com.intenthq.vertica"

version := "0.0.2"

crossPaths := false

autoScalaLibrary := false

publishMavenStyle := true

publishArtifact in Test := false

publishArtifact in packageDoc := false

scalacOptions += "-target:jvm-1.7"

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

libraryDependencies += "org.apache.hadoop" % "hadoop-mapreduce-client-core" % hadoopVer % "provided"

libraryDependencies += "org.apache.hadoop" % "hadoop-common" % hadoopVer % "provided"

libraryDependencies += "junit" % "junit" % "4.12" % "test"

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

autoAPIMappings := true

pomExtra := (
  <url>https://github.com/intenthq/pucket</url>
    <licenses>
      <license>
        <name>Apache License</name>
        <url>https://raw.githubusercontent.com/vertica/Vertica-Hadoop-Connector/master/LICENSE.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:vertica/Vertica-Hadoop-Connector.git</url>
      <connection>scm:git:git@github.com:vertica/Vertica-Hadoop-Connector.git</connection>
    </scm>
    <developers>
      <developer>
        <id>vertica</id>
        <name>Vertica</name>
      </developer>
    </developers>
  )

