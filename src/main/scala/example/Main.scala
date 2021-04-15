package example

import cats.implicits._
import example.HttpApp.{ endpoints, AppEnv }
import example.auth.Auths
import example.food.Foods
import example.pet.Pets
import org.http4s.HttpRoutes
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._
import org.http4s.server.middleware.CORS
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import zio._
import zio.clock._
import zio.interop.catz.{ taskConcurrentInstance, taskEffectInstance, zioContextShift, zioTimer }

object Main extends App {
  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    server
      .provideCustomLayer(Pets.live ++ Foods.live ++ Auths.live)
      .exitCode

  val restApi: HttpRoutes[RIO[AppEnv with Clock, *]] =
    ZHttp4sServerInterpreter
      .from(endpoints)
      .toRoutes

  val swagger: HttpRoutes[RIO[AppEnv with Clock, *]] = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._
    new SwaggerHttp4s(
      OpenAPIDocsInterpreter
        .serverEndpointsToOpenAPI(endpoints, "Our pets and what they eat", "1.0")
        .toYaml
    ).routes
  }

  val server: ZIO[zio.ZEnv with AppEnv, Throwable, Unit] =
    ZIO.runtime[ZEnv with AppEnv].flatMap { implicit runtime =>
      BlazeServerBuilder[RIO[AppEnv with Clock, *]](runtime.platform.executor.asEC)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(
          CORS(
            Router(
              "/" -> (restApi <+> swagger)
            ).orNotFound
          )
        )
        .serve
        .compile
        .drain
    }
}
