import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScalaJSReactReduxForm {
  object Versions {
    val scala = "2.11.8"
    val scalaTest = "3.0.1"
    val scalaJsReact = "0.11.3"
    val scalaJsRedux = "0.0.1-SNAPSHOT"

    val react = ">=15.4.2"
    val redux = ">=3.6.0"
  }

  object Dependencies {
    lazy val scalaTest = "org.scalatest" %%%! "scalatest" % Versions.scalaTest % "test"
    lazy val scalaJsRedux = "com.github.eldis" %%%! "scalajs-redux" % Versions.scalaJsRedux
    lazy val scalaJsReact = "com.github.japgolly.scalajs-react" %%%! "core" % Versions.scalaJsReact
  }

  object Settings {
    type PC = Project => Project

    def commonProject: PC =
      _.settings(
        organization := "com.github.eldis",
        organizationName := "Eldis-Soft, ZAO"
      )

    def usesScalaJS: PC =
      _.enablePlugins(ScalaJSPlugin)

    def usesScalaJSBundler: PC =
      _.enablePlugins(ScalaJSBundlerPlugin)

    def usesSonatype: PC =
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
        Settings.usesScalaJS,
        Settings.usesScalaJSBundler,
        Settings.usesSonatype
      ).settings(
        name := "scalajs-react-redux-form",

        libraryDependencies ++= Seq(
          Dependencies.scalaJsReact,
          Dependencies.scalaJsRedux,
          Dependencies.scalaTest
        ),

        npmDevDependencies in Test ++= Seq(
          "react" -> Versions.react
        ),

        unmanagedResourceDirectories in Test +=
          (sourceDirectory in Test).value / "javascript"
      )
  }
}
