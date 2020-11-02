package example

import cats.implicits._
import example.HttpApp.AppEnv
import example.auth.Auths
import example.food.Foods
import example.pet.Pets
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio._
import zio.clock._
import zio.interop.catz.{ taskEffectInstance, zioContextShift, zioTimer }

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    server
      .provideCustomLayer(Pets.live ++ Foods.live ++ Auths.live)
      .exitCode

  val server: ZIO[zio.ZEnv with AppEnv, Throwable, Unit] =
    ZIO.runtime[ZEnv with AppEnv].flatMap { implicit runtime =>
      BlazeServerBuilder[RIO[AppEnv with Clock, *]](runtime.platform.executor.asEC)
        .bindHttp(8080, "localhost")
        .withHttpApp(
          CORS(
            Router(
              "/" -> (HttpApp.routes <+> new SwaggerHttp4s(HttpApp.yaml).routes[RIO[AppEnv with Clock, *]])
            ).orNotFound
          )
        )
        .serve
        .compile
        .drain
    }
}
