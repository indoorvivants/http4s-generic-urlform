scalaVersion := "2.13.3"
crossScalaVersions := Seq("2.13.3", "2.12.12")
addCompilerPlugin(
  "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
)

name := "http4s-generic-urlform"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-core" % "0.21.6",
  "org.slf4j"   % "slf4j-nop"   % "2.0.0-alpha1"
)
libraryDependencies += "com.propensive" %% "magnolia"      % "0.16.0"
libraryDependencies += "org.scala-lang"  % "scala-reflect" % scalaVersion.value % Provided

inThisBuild(
  List(
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := scalaBinaryVersion.value,
    organization := "com.indoorvivants",
    homepage := Some(
      url("https://github.com/indoorvivants/http4s-generic-urlform")
    ),
    licenses := List(
      "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "keynmol",
        "Anton Sviridov",
        "keynmol@gmail.com",
        url("https://blog.indoorvivants.com")
      )
    )
  )
)

val scalafixRules = Seq(
  "OrganizeImports",
  "DisableSyntax",
  "LeakingImplicitClassVal",
  "ProcedureSyntax",
  "NoValInForComprehension"
).mkString(" ")

val CICommands = Seq(
  "clean",
  "compile",
  "test",
  "scalafmtCheckAll",
  s"scalafix --check $scalafixRules",
  "missinglinkCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"compile:scalafix --rules $scalafixRules",
  s"test:scalafix --rules $scalafixRules",
  "test:scalafmtAll",
  "compile:scalafmtAll",
  "scalafmtSbt",
  "missinglinkCheck"
).mkString(";")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
