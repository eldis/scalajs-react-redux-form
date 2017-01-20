import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
// import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
// import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScalaJSReactReduxForm {
  object Versions {
    val scala = "2.11.8"
    val scalaTest = "3.0.1"
    val scalaJsReact = "0.11.3"
    val scalaJsRedux = "0.0.1-SNAPSHOT"
  }

  object Dependencies {
    lazy val scalaTest = "org.scalatest" %%%! "scalatest" % Versions.scalaTest % "test"
    lazy val scalaJsRedux = "com.github.eldis" %%%! "scalajs-redux" % Versions.scalaJsRedux
  }

  object Settings {
    type PC = Project => Project

    def commonProject: PC =
      _.settings(
        scalaVersion := Versions.scala,
        organization := "com.github.eldis",
        organizationName := "Eldis-Soft, ZAO"
      )

    def scalajsProject: PC =
      _.enablePlugins(ScalaJSPlugin)

    def sonatypeResolver: PC =
      _.settings(
        resolvers ++= Seq(
          Resolver.sonatypeRepo("public"),
          Resolver.sonatypeRepo("snapshots")
        )
      )
  }

  object Projects {
    lazy val root = project.in(file("."))
      .configure(
        Settings.commonProject,
        Settings.scalajsProject,
        Settings.sonatypeResolver
      ).settings(
        name := "scalajs-react-redux-form",

        libraryDependencies ++= Seq(
          Dependencies.scalaJsRedux,
          Dependencies.scalaTest
        )
      )
  }
}
