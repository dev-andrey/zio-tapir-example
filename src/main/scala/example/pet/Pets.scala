package example.pet

import zio._
import zio.console._

object Pets {
  type Pets = Has[Pets.Service]

  trait Service {
    def getPets: IO[String, List[Pet]]
    def getPetById(id: Long): IO[String, Pet]
  }

  def getPets: ZIO[Pets, String, List[Pet]] =
    ZIO.accessM(_.get.getPets)

  def getPetById(id: Long): ZIO[Pets, String, Pet] =
    ZIO.accessM(_.get.getPetById(id))

  val live: ZLayer[Console, String, Pets] = ZLayer.fromService { console =>
    new Service {
      def getPets: IO[String, List[Pet]] =
        ZIO.effectTotal {
          List(
            Pet("Tapirus", "https://en.wikipedia.org/wiki/Tapir"),
            Pet("Panda", "https://en.wikipedia.org/wiki/Panda"),
          )
        }

      def getPetById(id: Long): IO[String, Pet] =
        for {
          _ <- console.putStrLn(s"Got request for pet: $id")
          pet <-
            if (id == 35) UIO(Pet("Tapirus terrestris", "https://en.wikipedia.org/wiki/Tapir"))
            else IO.fail(s"Unknown pet id $id")
        } yield pet
    }
  }
}
