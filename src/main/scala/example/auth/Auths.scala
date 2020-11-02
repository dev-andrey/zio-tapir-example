package example.auth

import example.ApiError
import zio._

object Auths {
  type Auths = Has[Auths.Service]

  trait Service {
    def auth(token: String): IO[ApiError, User]
  }

  def auth(token: String): ZIO[Auths, ApiError, User] =
    ZIO.accessM(_.get.auth(token))

  val live: ULayer[Auths] = ZLayer.succeed(new Service {
    override def auth(token: String): IO[ApiError, User] =
      if (token == "secret") IO.succeed(User("Tradey McTradeyface", 42))
      else IO.fail(ApiError.Unauthorized())
  })
}
