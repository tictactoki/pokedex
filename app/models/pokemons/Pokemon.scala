package models.pokemons

import play.api.libs.json.Json

/**
  * Created by wong on 29/04/17.
  */
case class Pokemon(id: String,
                   name: String,
                   base_experience: String,
                   height: Int,
                   is_default: Boolean,
                   order: Int,
                   weight: Int,
                   abilities: List[Ability],
                   forms: List[Data],
                   game_indices: List[GameIndices]
                  )

case class GameIndices(game_index: Int,
                       version: Data
                      )

case class Data(name: String,
                url: String)

object Data {
  implicit val dataFormat = Json.format[Data]
}

case class Ability(id_hidden: Boolean,
                   slot: Int,
                   ability: Data
                  )

object PokeData {
  implicit val pokeDataFormat = Json.format[PokeData]
}

case class PokeData(count: Int,
                    previous: Option[String],
                    results: List[Data],
                    next: Option[String]
                   )
