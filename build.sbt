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
        library.tapirZio,
        library.tapirZioHttp4s,
      )
    )

lazy val library =
  new {
    object v {
      val zio            = "1.0.3"
      val zioInteropCats = "2.2.0.1"
      val tapir          = "0.17.0-M6"
    }
    val zio            = "dev.zio" %% "zio"              % v.zio
    val zioTest        = "dev.zio" %% "zio-test"         % v.zio
    val zioTestSbt     = "dev.zio" %% "zio-test-sbt"     % v.zio
    val zioInteropCats = "dev.zio" %% "zio-interop-cats" % v.zioInteropCats

    val tapir          = "com.softwaremill.sttp.tapir" %% "tapir-core"              % v.tapir
    val tapirZio       = "com.softwaremill.sttp.tapir" %% "tapir-zio"               % v.tapir
    val tapirZioHttp4s = "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % v.tapir
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
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
  )
