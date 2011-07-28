scalaVersion := "2.9.0-1"


libraryDependencies ++= Seq(
  "org.jboss.netty" % "netty" % "3.2.4.Final",
  "com.google.collections" % "google-collections" % "1.0",
  "org.specs2" %% "specs2" % "1.5" % "test",
  "com.novocode" % "junit-interface" % "0.6" % "test",
  "com.lmax" % "disruptor" % "1.2.2-RELEASE"
)

resolvers ++= Seq(
  "scala" at "http://scala-tools.org/repo-releases",
  "scala snapshots" at "http://scala-tools.org/repo-snapshots",
  "disruptor-temporary" at "https://repository-helios.forge.cloudbees.com/release/"
)