package example

import example.auth.Auths
import example.auth.Auths.Auths
import example.food.Foods.Foods
import example.food.{ Food, Foods }
import example.pet.Pets.Pets
import example.pet.{ Pet, Pets }
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.circe._
import sttp.tapir.ztapir._

sealed trait ApiError
object ApiError {
  final case class Unauthorized(message: String = "unauthorized") extends ApiError
  final case class UserError(code: Int, message: String)          extends ApiError
}

object HttpApp {
  type AppEnv = Pets with Foods with Auths

  import ApiError._

  private val secure =
    endpoint
      .in(auth.bearer[String]())
      .errorOut(
        oneOf[ApiError](
          statusMapping(StatusCode.Unauthorized, jsonBody[Unauthorized].description("unauthorized")),
          statusDefaultMapping(jsonBody[UserError].description("user error")),
        )
      )
      .zServerLogicForCurrent(token => Auths.auth(token))

  private val getPetsById =
    endpoint
      .get
      .in("pets" / path[Long]("id"))
      .in(header[Option[String]]("Version"))
      .errorOut(stringBody)
      .out(jsonBody[Pet])

  private val getPets = endpoint.get.in("pets").errorOut(stringBody).out(jsonBody[List[Pet]])

  private val getFoods = secure.get.in("foods").out(jsonBody[List[Food]])

  private val secureEndpoints = List(
    getFoods.serverLogic(_ => Foods.getFoods).widen[AppEnv]
  )

  private val insecureEndpoints = List(
    getPets.zServerLogic(_ => Pets.getPets).widen[AppEnv],
    getPetsById.zServerLogic {
      case (id, Some("2.0")) => Pets.getPetWithFallBack(id)
      case (id, _)           => Pets.getPetById(id)
    }.widen[AppEnv],
  )

  val endpoints = secureEndpoints ++ insecureEndpoints
}
