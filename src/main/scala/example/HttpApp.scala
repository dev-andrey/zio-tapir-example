package example

import example.auth.Auths
import example.auth.Auths.Auths
import example.food.Foods.Foods
import example.food.{ Food, Foods }
import example.pet.Pets.Pets
import example.pet.{ Pet, Pets }
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.ztapir._
import sttp.tapir.ztapir._
import zio.RIO
import zio.clock._
import zio.interop.catz.{ taskConcurrentInstance, zioContextShift }

sealed trait ApiError
object ApiError {
  final case class Unauthorized(message: String = "unathorized") extends ApiError
  final case class UserError(code: Int, message: String)         extends ApiError
}

object HttpApp {
  type AppEnv = Pets with Foods with Auths

  import ApiError._
  private val secure =
    endpoint
      .in(auth.bearer[String])
      .errorOut(
        oneOf[ApiError](
          statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
          statusDefaultMapping(jsonBody[UserError].description("user error")),
        )
      )
      .zServerLogicForCurrent(token => Auths.auth(token))

  private val getPetByIdV2 =
    endpoint
      .get
      .in(header("Version", "2.0"))
      .in("pets" / path[Long]("id"))
      .errorOut(stringBody)
      .out(jsonBody[Pet])
  private val getPetById = endpoint.get.in("pets" / path[Long]("id")).errorOut(stringBody).out(jsonBody[Pet])
  private val getPets    = endpoint.get.in("pets").errorOut(stringBody).out(jsonBody[List[Pet]])

  private val getFoods = secure.get.in("foods").out(jsonBody[List[Food]])

  private val endpoints = List(
    getPets.zServerLogic(_ => Pets.getPets).widen[AppEnv],
    getPetByIdV2.zServerLogic(id => Pets.getPetById(id + 1)).widen[AppEnv],
    getPetById.zServerLogic(id => Pets.getPetById(id)).widen[AppEnv],
    getFoods.serverLogic(_ => Foods.getFoods).widen[AppEnv],
  )

  val routes: HttpRoutes[RIO[AppEnv with Clock, *]] =
    endpoints.toRoutes

  val yaml: String = {
    import sttp.tapir.docs.openapi._
    import sttp.tapir.openapi.circe.yaml._
    endpoints.toOpenAPI("Our pets and what they eat", "1.0").toYaml
  }
}
