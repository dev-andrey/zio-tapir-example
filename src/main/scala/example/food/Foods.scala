package example.food

import example.ApiError
import zio.{ Has, IO, ZIO, ZLayer }

object Foods {
  type Foods = Has[Foods.Service]
  trait Service {
    def getFoods: IO[ApiError, List[Food]]
  }

  def getFoods: ZIO[Foods, ApiError, List[Food]] =
    ZIO.accessM(_.get.getFoods)

  val live: ZLayer[Any, Nothing, Foods] = ZLayer.succeed(new Service {
    override def getFoods: IO[ApiError, List[Food]] = IO.effectTotal {
      List(
        Food("French Fries", Diet.Vegetarian),
        Food("Chicken Pot Pie", Diet.Omnivore),
        Food("Steak", Diet.Carnivore),
      )
    }
  })
}
