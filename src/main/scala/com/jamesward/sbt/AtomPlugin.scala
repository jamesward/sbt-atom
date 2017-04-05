package com.jamesward.sbt

import sbt._
import sbt.Keys._


object AtomPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  object autoImport {
    lazy val atomVersion: TaskKey[String] = taskKey[String]("Atom Version")
    lazy val atomReleases: TaskKey[Unit] = taskKey[Unit]("Atom Releases")
    lazy val atomExcludePrereleases: SettingKey[Boolean] = settingKey[Boolean]("Exclude Prereleases")
    lazy val atomOs: SettingKey[String] = settingKey[String]("Operating System")
    lazy val atomPackages: SettingKey[Seq[String]] = settingKey[Seq[String]]("Atom Packages")
    lazy val atomFilesToOpen: SettingKey[Seq[String]] = settingKey[Seq[String]]("Files to Open")
    lazy val atomHome: TaskKey[File] = taskKey[java.io.File]("Atom Home")
    lazy val atomDownload: TaskKey[Unit] = taskKey[Unit]("Download Atom and extract it to the Atom Home dir")
    lazy val atomInstallPackages: TaskKey[Unit] = taskKey[Unit]("Install Atom Packages")
    lazy val atomRun: TaskKey[Unit] = taskKey[Unit]("Run Atom")
    lazy val atom: TaskKey[Unit] = taskKey[Unit]("Download and run Atom")
  }

  import autoImport._

  lazy val baseAtomSettings: Seq[Def.Setting[_]] = Seq(
    atomExcludePrereleases := true,
    atomOs := sys.props.get("os.name").getOrElse(throw new Exception("Operating system could not be determined")),
    atomVersion := AtomUtils.latest(atomExcludePrereleases.value).name,
    atomReleases := AtomUtils.releases.foreach { release => state.value.log.info(release.name) },
    atomPackages := Seq.empty[String],
    atomFilesToOpen := Seq("./"),
    atomHome := sbt.Path.userHome / ".atom" / atomVersion.value,
    atomDownload := AtomUtils.download(atomVersion.value, atomHome.value, AtomUtils.OS.withName(atomOs.value)),
    atomInstallPackages := AtomUtils.installPackages(atomHome.value, AtomUtils.OS.withName(atomOs.value), atomPackages.value),
    atomRun := AtomUtils.run(atomHome.value, AtomUtils.OS.withName(atomOs.value), baseDirectory.value, atomFilesToOpen.value),
    atom := AtomUtils.launch(atomVersion.value, atomHome.value, AtomUtils.OS.withName(atomOs.value), atomPackages.value, baseDirectory.value, atomFilesToOpen.value)
  )

  override lazy val projectSettings: Seq[Def.Setting[_]] = super.projectSettings ++ baseAtomSettings

}
