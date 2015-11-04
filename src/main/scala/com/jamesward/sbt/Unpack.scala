package com.jamesward.sbt

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

import org.apache.commons.compress.archivers.zip.ZipFile
import sbt.IO

object Unpack {
  import scala.collection.JavaConverters._

  def apply(file: java.io.File, destination: java.io.File): Unit = {

    val zipFile = new ZipFile(file)

    zipFile.getEntries.asScala.foreach { zipArchiveEntry =>

      if (zipArchiveEntry.isDirectory) {
        IO.createDirectory(new File(destination, zipArchiveEntry.getName))
      }
      else if (zipArchiveEntry.isUnixSymlink) {
        val link = new File(destination, zipArchiveEntry.getName)
        val target = new File(zipFile.getUnixSymlink(zipArchiveEntry))
        Files.createSymbolicLink(link.toPath, target.toPath)
      }
      else {
        val zipArchiveEntryInputStream = zipFile.getInputStream(zipArchiveEntry)
        val file = new File(destination, zipArchiveEntry.getName)
        IO.transfer(zipArchiveEntryInputStream, file)
        zipArchiveEntryInputStream.close()
        Files.setPosixFilePermissions(file.toPath, unixModeToPermSet(zipArchiveEntry.getUnixMode).asJava)
      }
    }

    zipFile.close()
  }

  def unixModeToPermSet(mode: BigInt): Set[PosixFilePermission] = {
    Map(
      PosixFilePermission.OWNER_READ -> BigInt("0400", 8),
      PosixFilePermission.OWNER_WRITE -> BigInt("0200", 8),
      PosixFilePermission.OWNER_EXECUTE -> BigInt("0100", 8),
      PosixFilePermission.GROUP_READ -> BigInt("0040", 8),
      PosixFilePermission.GROUP_WRITE -> BigInt("0020", 8),
      PosixFilePermission.GROUP_EXECUTE -> BigInt("0010", 8),
      PosixFilePermission.OTHERS_READ -> BigInt("0004", 8),
      PosixFilePermission.OTHERS_WRITE -> BigInt("0002", 8),
      PosixFilePermission.OTHERS_EXECUTE -> BigInt("0001", 8)
    ).filter { case (perm, mask) =>
      (mode | mask) == mode
    }.keySet
  }

}