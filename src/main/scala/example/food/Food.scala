package example.food

final case class Food(name: String, kind: Diet)

sealed trait Diet
object Diet {
  case object Vegetarian extends Diet
  case object Carnivore  extends Diet
  case object Omnivore   extends Diet
}
