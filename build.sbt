sbtPlugin := true

organization := "com.jamesward"

name := "sbt-atom"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.3",
  "org.apache.commons" % "commons-compress" % "1.10",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)

publishMavenStyle := false

enablePlugins(GitVersioning)

git.useGitDescribe := true
