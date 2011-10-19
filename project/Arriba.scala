import sbt._
import sbt.Keys._

object Arriba extends Build {

  override def projects = Seq(
    root
  )
  lazy val root = Project("root", file("."), settings = buildSettings)

  val arribaMainClass: String = "arriba.server.Both"
  val buildSettings = {
    Defaults.defaultSettings ++
      Seq(testFrameworks += specs2Framework,
        libraryDependencies ++= dependencies,
        mainClass := Some(arribaMainClass),
      moduleConfigurations ++= arribaConfigurations
      )
  }

  val dependencies = Seq(
    "junit" % "junit" % "4.8" % "test",
    "org.jboss.netty" % "netty" % "3.2.4.Final",
    "com.google.collections" % "google-collections" % "1.0",
    "org.specs2" %% "specs2" % "1.5" % "test",
    "com.novocode" % "junit-interface" % "0.6" % "test",
//    "com.lmax" % "disruptor" % "2",
    "com.weiglewilczek.slf4s" % "slf4s_2.8.1" % "1.0.4",
    "org.slf4j" % "slf4j-simple" % "1.6.1"
  )


  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")

  val Disruptor =  "disruptor" at "https://repository-helios.forge.cloudbees.com/release/"
  val ScalaSnapshots = "scala snapshots" at "http://scala-tools.org/repo-snapshots"
  val ScalaReleases = "scala" at "http://scala-tools.org/repo-releases"

  val arribaConfigurations = Seq(
    ModuleConfiguration("com.lmax.disruptor", Disruptor),
  ModuleConfiguration("com.weiglewilczek.slf4s" , ScalaReleases)
  )
}