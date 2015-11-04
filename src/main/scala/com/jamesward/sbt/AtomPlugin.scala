package com.jamesward.sbt

import sbt._
import sbt.Keys._


object AtomPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport {
    lazy val atomVersion = settingKey[String]("Atom Version")
    lazy val atomExcludePrereleases = settingKey[Boolean]("Exclude Prereleases")
    lazy val atomOs = settingKey[String]("Operating System")
    lazy val atomPackages = settingKey[Seq[String]]("Atom Packages")
    lazy val atomFilesToOpen = settingKey[Seq[String]]("Files to Open")
    lazy val atomHome = settingKey[java.io.File]("Atom Home")
    lazy val atomDownload = taskKey[Unit]("Download Atom and extract it to the Atom Home dir")
    lazy val atomInstallPackages = taskKey[Unit]("Install Atom Packages")
    lazy val atomRun = taskKey[Unit]("Run Atom")
    lazy val atom = taskKey[Unit]("Download and run Atom")
  }

  import autoImport._

  lazy val baseAtomSettings: Seq[Def.Setting[_]] = Seq(
    atomExcludePrereleases := true,
    atomOs := sys.props.get("os.name").getOrElse(throw new Exception("Operating system could not be determined")),
    atomVersion := AtomUtils.latest(atomExcludePrereleases.value).name,
    atomPackages := Seq.empty[String],
    atomFilesToOpen := Seq("./"),
    atomHome := sbt.Path.userHome / ".atom" / atomVersion.value,
    atomDownload := AtomUtils.download(atomVersion.value, atomHome.value, AtomUtils.OS.withName(atomOs.value)),
    atomInstallPackages := AtomUtils.installPackages(atomHome.value, AtomUtils.OS.withName(atomOs.value), atomPackages.value),
    atomRun := AtomUtils.run(atomHome.value, AtomUtils.OS.withName(atomOs.value), baseDirectory.value, atomFilesToOpen.value),
    atom := AtomUtils.launch(atomVersion.value, atomHome.value, AtomUtils.OS.withName(atomOs.value), atomPackages.value, baseDirectory.value, atomFilesToOpen.value)
  )

  override lazy val projectSettings = super.projectSettings ++ baseAtomSettings

}