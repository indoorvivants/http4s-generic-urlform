lazy val core = project
  .in(file("."))
  .settings(
    scalaVersion := "2.13.3",
    crossScalaVersions := Seq("2.13.3", "2.12.12"),
    name := "http4s-generic-urlform",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-core" % "0.21.6",
      "org.slf4j"   % "slf4j-nop"   % "2.0.0-alpha1"
    ),
    libraryDependencies += "com.propensive"      %% "magnolia"          % "0.16.0",
    libraryDependencies += "org.scala-lang"       % "scala-reflect"     % scalaVersion.value % Provided,
    libraryDependencies += "com.disneystreaming" %% "weaver-framework"  % "0.5.0"            % Test,
    libraryDependencies += "com.disneystreaming" %% "weaver-scalacheck" % "0.5.0"            % Test,
    testFrameworks += new TestFramework("weaver.framework.TestFramework"),

    fork in Test := true
  )

lazy val docs = project
  .in(file("myproject-docs"))
  .dependsOn(core)
  .enablePlugins(MdocPlugin)
  .settings(
    scalaVersion := (core / scalaVersion).value
  )

inThisBuild(
  List(
    scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.0",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := scalaBinaryVersion.value,
    organization := "com.indoorvivants",
    organizationName := "Anton Sviridov",
    homepage := Some(
      url("https://github.com/indoorvivants/http4s-generic-urlform")
    ),
    startYear := Some(2020),
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
  "missinglinkCheck",
  "checkReadme",
  "headerCheck"
).mkString(";")

val PrepareCICommands = Seq(
  s"compile:scalafix --rules $scalafixRules",
  s"test:scalafix --rules $scalafixRules",
  "test:scalafmtAll",
  "compile:scalafmtAll",
  "scalafmtSbt",
  "missinglinkCheck",
  "checkReadme",
  "headerCreate"
).mkString(";")

addCommandAlias("checkReadme", "docs/mdoc --in README.md")

addCommandAlias("ci", CICommands)

addCommandAlias("preCI", PrepareCICommands)
