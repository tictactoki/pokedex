package models.helpers

/**
  * Created by wong on 02/05/17.
  */
object MongoDBFields {

  final val Id = "_id"
  final val Name = "name"
  final val Weight = "weight"
  final val Height = "height"
  final val Stats = "stats"
  final val Types = "types"
  final val Sprites = "sprites"
  final val Password = "password"
  final val Pokemon = "pokemon"

}

object MongoCollection {
  final val Users = "users"
  final val Pokemons = "pokemons"
  final val Bookmarked = "bookmarked"
}