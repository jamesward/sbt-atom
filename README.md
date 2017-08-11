Atom sbt Plugin
=====================


Usage
-----

Add the plugin in your `project/plugins.sbt` file:

    addSbtPlugin("com.jamesward" % "sbt-atom" % "0.0.4")


Enable the plugin and set its config in your `build.sbt` file:

    enablePlugins(AtomPlugin)
    
Launch Atom:

    sbt atom

Optional sbt Settings (`build.sbt`):

    atomVersion := "1.0.0"              // default is the latest release from: https://github.com/atom/atom/releases
    atomExcludePrereleases := false     // default is true (exclude prereleases)
    atomOs := "Windows"                 // default is your OS
    atomPackages := Seq("heroku-tools") // default is none
    atomFilesToOpen := Seq("README.md") // default is Seq("./") i.e. the project dir
    atomHome := file("/foo")            // default is ~/.atom


Developer Info
--------------

Test Project:

    cd test-project
    ../sbt atom

Release:

1. Replace version in `README.md`
1. Commit changes: `git commit -am "release 0.0.x"`
1. Tag the release: `git tag v0.0.x`
1. Push tag and changes: `git push --follow-tags`
1. Publish the release: `./sbt publish`
