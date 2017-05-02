package models.pokemons

import play.api.libs.json.{JsObject, JsValue, Json}

/**
  * Created by wong on 29/04/17.
  */

case class Sprites(back_female: Option[String],
                   back_shiny_female: Option[String],
                   back_default: Option[String],
                   front_female: Option[String],
                   front_shiny_female: Option[String],
                   back_shiny: Option[String],
                   front_default: Option[String],
                   front_shiny: Option[String]
                  )

object Sprites {
  implicit val spritesFormat = Json.format[Sprites]
}

case class Couple(name: String,
                  url: String)

object Couple {
  implicit val dataFormat = Json.format[Couple]
}

case class DataName(name: String, language: Couple)

object DataName {
  implicit val dataNameFormat = Json.format[DataName]
}

case class Stat(stat: Couple, effort: Int, base_stat: Int)

object Stat {
  implicit val statFormat = Json.format[Stat]
}

case class Type(slot: Int, `type`: Couple)

object Type {
  implicit val typeFomart = Json.format[Type]
}

case class Ability(id: Int,
                   name: String,
                   ability: Couple

                  )

object PokeData {
  implicit val pokeDataFormat = Json.format[PokeData]
}

case class PokeData(count: Int,
                    previous: Option[String],
                    results: List[Couple],
                    next: Option[String]
                   )


