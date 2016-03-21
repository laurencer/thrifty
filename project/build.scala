import sbt._
import Keys._

import au.com.cba.omnia.uniform.core.standard.StandardProjectPlugin._
import au.com.cba.omnia.uniform.core.version.UniqueVersionPlugin._
import au.com.cba.omnia.uniform.dependency.UniformDependencyPlugin._
import au.com.cba.omnia.uniform.assembly.UniformAssemblyPlugin._

object build extends Build {
  type Sett = Def.Setting[_]

  lazy val standardSettings: Seq[Sett] =
    Defaults.coreDefaultSettings ++
    uniformDependencySettings ++
    strictDependencySettings ++
    Seq(
      updateOptions := updateOptions.value.withCachedResolution(true)
    )

  lazy val thrifty = Project(
    id = "thrifty"
    , base = file(".")
    , settings =
      standardSettings
        ++ uniform.project("thrifty", "com.rouesnel.thrifty")
        ++ Seq[Sett](
          crossScalaVersions := Seq("2.11.0", "2.10.4")
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
