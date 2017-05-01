package models.pokemons

import play.api.libs.json.Json

/**
  * Created by wong on 29/04/17.
  */

case class Sprites(back_default: Option[String],
                   back_shiny: Option[String],
                   front_default: Option[String],
                   front_shiny: Option[String]
                  )

object Sprites {
  implicit val spritesFormat = Json.format[Sprites]
}

case class Data(name: String,
                url: String)

object Data {
  implicit val dataFormat = Json.format[Data]
}

case class DataName(name: String, language: Data)

object DataName {
  implicit val dataNameFormat = Json.format[DataName]
}

case class PokemonForm(id: Int,
                       name: String,
                       order: Int,
                       form_order: Int,
                       is_default: Boolean,
                       is_battle_only: Boolean,
                       is_mega: Boolean,
                       form_name: String,
                       pokemon: Data,
                       sprites: Sprites,
                       version_group: Data,
                       names: List[DataName] = Nil,
                       form_names: List[DataName] = Nil
                      )

object PokemonForm {
  implicit val pokemonFormFormat = Json.format[PokemonForm]
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
