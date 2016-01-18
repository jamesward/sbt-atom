package com.jamesward.sbt

import java.net.URL


import play.api.libs.json._
import play.api.libs.functional.syntax._
import sbt._


object AtomUtils {

  object OS extends Enumeration {
    type OS = Value
    val Mac = Value("Mac OS X")
    val Windows = Value("Windows")
    val Linux = Value("Linux")
  }

  case class Asset(name: String, downloadUrl: String)

  case object Asset {
    implicit val jsonReads = (
      (__ \ "name").read[String] ~
      (__ \ "browser_download_url").read[String]
    )(Asset.apply _)
  }

  case class Release(name: String, prerelease: Boolean, assets: Seq[Asset])

  object Release {
    implicit val jsonReads = Json.reads[Release]
  }

  lazy val releases: Seq[Release] = {
    val jsonString = IO.readLinesURL(new URL("https://api.github.com/repos/atom/atom/releases")).mkString
    Json.parse(jsonString).as[Seq[Release]]
  }

  def release(version: String): Release = {
    releases.find(_.name == version).getOrElse(throw new Exception(s"Atom version $version could not be found"))
  }

  def latest(excludePrereleases: Boolean = true): Release = {
    releases.filterNot(_.prerelease && excludePrereleases).head
  }

  def atomExec(atomHome: java.io.File, os: OS.OS): java.io.File = {
    os match {
      case OS.Windows => atomHome / "Atom" / "atom.exe"
      case OS.Mac => atomHome / "Atom.app" / "Contents" / "Resources" / "app" / "atom.sh"
      //case OS.Mac => atomHome / "Atom.app" / "Contents" / "MacOS" / "atom"
      case _ => throw new Exception("Atom does not currently have a release archive for your operating system.")
    }
  }

  def apmExec(atomHome: java.io.File, os: OS.OS): java.io.File = {
    os match {
      case OS.Windows => atomHome / "Atom" / "resources" / "app" / "apm" / "bin" / "apm.cmd"
      case OS.Mac =>  atomHome / "Atom.app" / "Contents" / "Resources" / "app" / "apm" / "bin" / "apm"
      case _ => throw new Exception("Atom does not currently have a release archive for your operating system.")
    }
  }

  def download(version: String, atomHome: java.io.File, os: OS.OS): Unit = {

    def unzipAsset(name: String): Unit = {
      val asset = release(version).assets.find(_.name == name).getOrElse(throw new Exception(s"Could not find the release asset: $name"))
      IO.createDirectory(atomHome)
      val prefix = name.split('.').head
      val postfix = name.split('.').last
      IO.withTemporaryFile(prefix, s".$postfix") { atomZipFile =>

        IO.download(new URL(asset.downloadUrl), atomZipFile)
        Unpack(atomZipFile, atomHome)
      }
    }

    os match {
      case OS.Windows => unzipAsset("atom-windows.zip")
      case OS.Mac => unzipAsset("atom-mac.zip")
      case _ => throw new Exception("Atom does not currently have a release archive for your operating system.")
    }
  }

  def installPackages(atomHome: java.io.File, os: OS.OS, packages: Seq[String]): Unit = {
    import scala.sys.process._

    val apm = apmExec(atomHome, os)

    val apmList = Seq(apm.getAbsolutePath, "list").!!.linesIterator

    packages.foreach { packageName =>
      if (apmList.exists(_.contains(packageName))) {
        val exitCode = Seq(apm.getAbsolutePath, "upgrade", "-c", "false", packageName).!(ProcessLogger(_ => Unit))
        if (exitCode != 0) throw new Exception(s"Could not upgrade the Atom package: $packageName")
      }
      else {
        val exitCode = Seq(apm.getAbsolutePath, "install", "-s", packageName).!(ProcessLogger(_ => Unit))
        if (exitCode != 0) throw new Exception(s"Could not install the Atom package: $packageName")
      }
    }
  }

  def run(atomHome: java.io.File, os: OS.OS, cwd: File, filesToOpen: Seq[String]): Unit = {
    import scala.sys.process._

    val atom = atomExec(atomHome, os)

    val cmd = atom.getAbsolutePath +: filesToOpen

    Process(cmd, cwd, "ATOM_PATH" -> atomHome.getAbsolutePath).run(new ProcessIO(_ => Unit, _ => Unit, _ => Unit, true))
  }

  def launch(version: String, atomHome: java.io.File, os: OS.OS, packages: Seq[String], cwd: File, filesToOpen: Seq[String]): Unit = {
    if (!atomHome.exists()) {
      download(version, atomHome, os)
    }

    installPackages(atomHome, os, packages)

    run(atomHome, os, cwd, filesToOpen)
  }

}