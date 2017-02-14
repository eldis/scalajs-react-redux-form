import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

object ScalaJSReactReduxForm {
  object Versions {
    val scala = "2.11.8"
    val scalaJsReact = "0.1.0-SNAPSHOT"
    val scalaJsRedux = "0.3.0-SNAPSHOT"

    val scalatest = "3.0.1"
  }

  object JsVersions {
    val htmlWebpackPlugin = "~2.26.0"
    val htmlLoader = "~0.4.3"

    val react = "~15.4.2"
    val redux = "~3.6.0"
    val reactRedux = "~5.0.2"
    val reactReduxForm = "~1.5.3"
    val reduxLogger = "~2.7.4"
  }
  object Dependencies {
    lazy val scalaJsReact = "com.github.eldis" %%%! "scalajs-react" % Versions.scalaJsReact
    lazy val scalaJsReactCompat = "com.github.eldis" %%%! "scalajs-react-compat" % Versions.scalaJsReact

    lazy val scalaJsRedux = "com.github.eldis" %%%! "scalajs-redux-react-eldis" % Versions.scalaJsRedux

    lazy val scalatest = "org.scalatest" %%%! "scalatest" % Versions.scalatest % "test"

    lazy val jsReactReduxForm = Seq(
      "react" -> JsVersions.react,
      "react-dom" -> JsVersions.react,
      "redux" -> JsVersions.redux,
      "react-redux" -> JsVersions.reactRedux,
      "react-redux-form" -> JsVersions.reactReduxForm
    )

    lazy val jsReduxLogger = "redux-logger" -> JsVersions.reduxLogger
  }

  object Settings {
    type PC = Project => Project

    def commonProject: PC =
      _.settings(
        scalaVersion := Versions.scala,
        organization := "com.github.eldis"
      )

    def scalajsProject: PC =
      _.configure(commonProject)
      .enablePlugins(ScalaJSPlugin)
      .settings(
        requiresDOM in Test := true
      )

    def jsBundler: PC =
      _.enablePlugins(ScalaJSBundlerPlugin)
      .settings(
        enableReloadWorkflow := false,
        libraryDependencies += Dependencies.scalatest,
        npmDevDependencies in Test ++= Seq(
          "redux" -> JsVersions.redux
        )
      )

    def react(dev: Boolean = false): PC =
      _.settings(
        libraryDependencies ++= Seq(
          Dependencies.scalaJsReact,
          Dependencies.scalaJsReactCompat,
          Dependencies.scalaJsRedux
        ),
        if(dev)
          npmDevDependencies in Compile ++= Dependencies.jsReactReduxForm
        else
          npmDependencies in Compile ++= Dependencies.jsReactReduxForm
      )

    def exampleProject(prjName: String, useReact: Boolean = false): PC = { p: Project =>
      p.in(file("examples") / prjName)
        .configure(scalajsProject, jsBundler, react())
        .settings(
          name := prjName,

          npmDevDependencies in Compile ++= Seq(
            "html-webpack-plugin" -> JsVersions.htmlWebpackPlugin,
            "html-loader" -> JsVersions.htmlLoader
          ),

          webpackConfigFile in fastOptJS := Some(baseDirectory.value / "config" / "webpack.config.js"),
          webpackConfigFile in fullOptJS := Some(baseDirectory.value / "config" / "webpack.config.js"),

          npmDependencies in Compile += Dependencies.jsReduxLogger
        )
      }

    def publish: PC =
      _.settings(
        publishMavenStyle := true,
        publishTo := {
          val nexus = "https://oss.sonatype.org/"
          if (isSnapshot.value)
            Some("snapshots" at nexus + "content/repositories/snapshots")
          else
            Some("releases"  at nexus + "service/local/staging/deploy/maven2")
        }
      )
  }

  object Projects {
    lazy val scalaJsReactReduxForm = project.in(file("."))
      .configure(
        Settings.scalajsProject, Settings.jsBundler, Settings.publish, Settings.react(true)
      )
      .settings(
        name := "scalajs-react-redux-form"
      )

    lazy val exRaw = project
      .configure(
        Settings.exampleProject("raw")
      )
      .dependsOn(scalaJsReactReduxForm)
  }
}

