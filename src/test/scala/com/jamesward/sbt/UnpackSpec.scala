package com.jamesward.sbt

import java.nio.file.attribute.PosixFilePermission

import org.scalatest.{Matchers, WordSpec}

class UnpackSpec extends WordSpec with Matchers {

  "unixModeToPermSet" should {
    "convert a mode" in {
      Unpack.unixModeToPermSet(BigInt("0100", 8)) should equal (Set(PosixFilePermission.OWNER_EXECUTE))
      Unpack.unixModeToPermSet(BigInt("0200", 8)) should equal (Set(PosixFilePermission.OWNER_WRITE))
      Unpack.unixModeToPermSet(BigInt("0300", 8)) should equal (Set(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_WRITE))
      Unpack.unixModeToPermSet(BigInt("0400", 8)) should equal (Set(PosixFilePermission.OWNER_READ))
      Unpack.unixModeToPermSet(BigInt("0500", 8)) should equal (Set(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ))
      Unpack.unixModeToPermSet(BigInt("0600", 8)) should equal (Set(PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ))
      Unpack.unixModeToPermSet(BigInt("0700", 8)) should equal (Set(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ))
      Unpack.unixModeToPermSet(BigInt("0010", 8)) should equal (Set(PosixFilePermission.GROUP_EXECUTE))
      Unpack.unixModeToPermSet(BigInt("0020", 8)) should equal (Set(PosixFilePermission.GROUP_WRITE))
      Unpack.unixModeToPermSet(BigInt("0030", 8)) should equal (Set(PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_WRITE))
      Unpack.unixModeToPermSet(BigInt("0040", 8)) should equal (Set(PosixFilePermission.GROUP_READ))
      Unpack.unixModeToPermSet(BigInt("0050", 8)) should equal (Set(PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ))
      Unpack.unixModeToPermSet(BigInt("0060", 8)) should equal (Set(PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_READ))
      Unpack.unixModeToPermSet(BigInt("0070", 8)) should equal (Set(PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_READ))
      Unpack.unixModeToPermSet(BigInt("0001", 8)) should equal (Set(PosixFilePermission.OTHERS_EXECUTE))
      Unpack.unixModeToPermSet(BigInt("0002", 8)) should equal (Set(PosixFilePermission.OTHERS_WRITE))
      Unpack.unixModeToPermSet(BigInt("0003", 8)) should equal (Set(PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE))
      Unpack.unixModeToPermSet(BigInt("0004", 8)) should equal (Set(PosixFilePermission.OTHERS_READ))
      Unpack.unixModeToPermSet(BigInt("0005", 8)) should equal (Set(PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ))
      Unpack.unixModeToPermSet(BigInt("0006", 8)) should equal (Set(PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_READ))
      Unpack.unixModeToPermSet(BigInt("0007", 8)) should equal (Set(PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_READ))
      Unpack.unixModeToPermSet(BigInt("0777", 8)).size should equal (9)
      Unpack.unixModeToPermSet(BigInt("100644", 8)) should equal (Set(PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ, PosixFilePermission.OTHERS_READ))
    }
  }

}
