lazy val `zio-tapir-example` =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        library.zio,
        library.zioInteropCats,
        library.zioTest    % Test,
        library.zioTestSbt % Test,
        library.tapir,
        library.tapirSwagger,
        library.tapirRedoc,
        library.tapirCirce,
        library.tapirZio,
        library.tapirZioHttp4s,
        library.circeExtras,
        library.tapirOpenApiDocs,
        library.tapirOpenApiDocsCirce,
      )
    )
    .settings(
      addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    )

lazy val library =
  new {
    object v {
      val zio            = "1.0.3"
      val zioInteropCats = "2.2.0.1"
      val tapir          = "0.17.0-M6"
      val circe          = "0.13.0"
    }
    val zio            = "dev.zio" %% "zio"              % v.zio
    val zioTest        = "dev.zio" %% "zio-test"         % v.zio
    val zioTestSbt     = "dev.zio" %% "zio-test-sbt"     % v.zio
    val zioInteropCats = "dev.zio" %% "zio-interop-cats" % v.zioInteropCats

    val tapir                 = "com.softwaremill.sttp.tapir" %% "tapir-core"               % v.tapir
    val tapirCirce            = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % v.tapir
    val tapirZio              = "com.softwaremill.sttp.tapir" %% "tapir-zio"                % v.tapir
    val tapirSwagger          = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"  % v.tapir
    val tapirRedoc            = "com.softwaremill.sttp.tapir" %% "tapir-redoc-http4s"       % v.tapir
    val tapirZioHttp4s        = "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server"  % v.tapir
    val tapirOpenApiDocs      = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % v.tapir
    val tapirOpenApiDocsCirce = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % v.tapir

    val circeExtras = "io.circe" %% "circe-generic-extras" % v.circe

  }

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.3",
    organization := "dev.alebe",
    startYear := Some(2020),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-Ywarn-unused:imports",
    ) ++ Seq("-encoding", "UTF-8"),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    scalafmtOnCompile := true,
  )
