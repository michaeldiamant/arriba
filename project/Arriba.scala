import sbt._
import sbt.Keys._

object Arriba extends Build {

  lazy val root = Project("root", file(".")) settings(
    testFrameworks += specs2Framework,
    libraryDependencies += "junit" % "junit" % "4.8" % "test",
    mainClass := Some("arriba.server.Both")
    )

  override def projects = Seq(
    root
  )

  def specs2Framework = new TestFramework("org.specs2.runner.SpecsFramework")
}