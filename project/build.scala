import sbt._, Keys._

import com.typesafe.sbt.pgp.PgpKeys._

import xerial.sbt.Sonatype.SonatypeCommand._

import au.com.cba.omnia.uniform.core.standard.StandardProjectPlugin._
import au.com.cba.omnia.uniform.core.version.UniqueVersionPlugin._
import au.com.cba.omnia.uniform.dependency.UniformDependencyPlugin._
import au.com.cba.omnia.uniform.assembly.UniformAssemblyPlugin._

object build extends Build {
  type Sett = Def.Setting[_]

  // This is needed because otherwise we'll create two Sonatype staging repositories
  // and then we won't know which to release. This command ensures that we can
  // do a cross-build (i.e. sbt + publishToSonatype) which does all the operations
  // in the right order (e.g. publish + release for each cross-build separately).
  lazy val publishToSonatype = Command.command("publishToSonatype") { state =>
    val Some((updatedState, _)) = Project.runTask(publishSigned, state)
    Command.process("sonatypeRelease", updatedState)
  }

  lazy val standardSettings: Seq[Sett] =
    Defaults.coreDefaultSettings ++
    uniformDependencySettings ++
    strictDependencySettings ++
    Seq(
      updateOptions := updateOptions.value.withCachedResolution(true)
    , commands += publishToSonatype
    )

  lazy val thrifty = Project(
    id = "thrifty"
    , base = file(".")
    , settings =
      standardSettings
        ++ uniform.project("thrifty", "com.rouesnel.thrifty")
        ++ Seq[Sett](
          organization := "com.rouesnel"
        , crossScalaVersions := Seq("2.11.7", "2.10.6")
        , scalacOptions := {
          val version = scalaVersion.value
          val options = scalacOptions.value
          if (version.startsWith("2.10.")) {
            // Not supported options in 2.10.x
            options.filter(o => o != "-Ywarn-unused-import")
          } else options
        }
        , libraryDependencies ++= {
            depend.testing(specs = "3.6.5", scalacheck = "1.12.5") ++ {
              val version = scalaVersion.value
              // The scala-parser-combinators library was only extracted from the
              // core of Scala in 2.11.
              if (version.startsWith("2.11.")) Seq("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4")
              else if (version.startsWith("2.10.")) Nil
              else throw new Exception(s"Explicitly handle new Scala versions ($version).")
            }}
      )
  )
}
