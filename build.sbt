sbtPlugin := true

organization := "com.jamesward"

name := "sbt-atom"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

scalaVersion := "2.10.6"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.10",
  "org.apache.commons" % "commons-compress" % "1.10",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishMavenStyle := false

//bintrayOrganization := Some("sbt-plugins")

enablePlugins(GitVersioning)

git.useGitDescribe := true
