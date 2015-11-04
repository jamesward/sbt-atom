lazy val atomPlugin = file("..").getAbsoluteFile.toURI

lazy val root = Project("test-project", file(".")).dependsOn(atomPlugin)
