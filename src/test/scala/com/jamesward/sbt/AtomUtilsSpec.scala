package com.jamesward.sbt

import java.io.File
import java.nio.file.Files

import org.scalatest.{Matchers, WordSpec}
import sbt._

class AtomUtilsSpec extends WordSpec with Matchers {

  "AtomUtils.latest" should {
    "find the latest version" in {
      AtomUtils.latest().name should be ("1.4.0")
      AtomUtils.latest(false).name should be ("1.4.0")
    }
  }

  "AtomUtils.installPackages" should {
    "install a package" in {
      IO.withTemporaryDirectory { tmpDir =>
        val os = AtomUtils.OS.withName(sys.props("os.name"))

        AtomUtils.download(AtomUtils.latest().name, tmpDir, os)
        AtomUtils.apmExec(tmpDir, os) should be a 'file

        try {
          AtomUtils.installPackages(tmpDir, os, Seq("heroku-tools"))
          new File(sys.props.get("user.home").get, ".atom/.apm/heroku-tools") should exist
        }
        catch {
          case e: Exception => fail(e.getMessage)
        }
      }
    }
  }

  "AtomUtils.download" should {
    "download a Mac release" in {
      IO.withTemporaryDirectory { tmpDir =>
        val os = AtomUtils.OS.Mac

        AtomUtils.download(AtomUtils.latest().name, tmpDir, os)

        val electronFramework = tmpDir / "Atom.app" / "Contents" / "Frameworks" / "Electron Framework.framework" / "Electron Framework"

        electronFramework should be a 'file
        Files.isSymbolicLink(electronFramework.toPath) should be (true)
        AtomUtils.apmExec(tmpDir, os) should be a 'file
        AtomUtils.apmExec(tmpDir, os) should be a 'canExecute
        AtomUtils.atomExec(tmpDir, os) should be a 'file

        // if we are running these tests on mac, then try to use Atom
        if (AtomUtils.OS.withName(sys.props("os.name")) == AtomUtils.OS.Mac) {
          try {
            AtomUtils.installPackages(tmpDir, os, Seq("heroku-tools"))
            AtomUtils.run(tmpDir, os, tmpDir, Seq.empty[String])
          }
          catch {
            case e: Exception => fail(e.getMessage)
          }
        }
      }
    }
    "download a Windows release" in {
      IO.withTemporaryDirectory { tmpDir =>
        val os = AtomUtils.OS.Windows

        AtomUtils.download(AtomUtils.latest().name, tmpDir, os)
        AtomUtils.apmExec(tmpDir, os).exists() should be (true)
        AtomUtils.atomExec(tmpDir, os).exists() should be (true)
      }
    }
    "fail for other operating systems" in {
      try {
        AtomUtils.download(AtomUtils.latest().name, new File(""), AtomUtils.OS.Linux)
        fail("An exception should have been thrown")
      }
      catch {
        case e: Exception =>
          e.getMessage should equal ("Atom does not currently have a release archive for your operating system.")
      }
    }
  }

}
