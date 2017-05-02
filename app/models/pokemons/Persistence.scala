package models.pokemons

import play.api.libs.json._
import models.helpers.Generator._
import play.api.data.Form
import play.api.data._
import play.api.data.Forms._
import models.helpers.{MongoDBFields => CF}
import play.modules.reactivemongo.json._, ImplicitBSONHandlers._
import reactivemongo.play.json._

/**
  * Created by wong on 02/05/17.
  */
sealed trait Persistence {
  val id: String = generateBSONId
  protected val dataType: String
}

object Persistence {

  object DataType {
    final val Pokemon = "pokemon"
    final val User = "user"
    final val BookMark = "bookmark"
  }

  implicit val persistenceReader: Reads[Persistence] = new Reads[Persistence] {
    override def reads(json: JsValue) = json match {
      case obj: JsObject => {
        (obj \ "dataType").as[String] match {
          case DataType.Pokemon => Pokemon.pokemonReader.reads(json)
          case DataType.User => User.userReader.reads(json)
        }
      }
      case _ => throw new Exception("No json object matched")
    }
  }

  implicit val persistenceWriter: OWrites[Persistence] = new OWrites[Persistence] {
    override def writes(o: Persistence): JsObject = o match {
      case p: Pokemon => Pokemon.pokemonWriter.writes(p)
      case u: User => User.userWriter.writes(u)
      case _ => throw new Exception("No writer defined")
    }
  }

}

object Pokemon {
  implicit val pokemonReader: Reads[Pokemon] = new Reads[Pokemon] {
    override def reads(json: JsValue) = json match {
      case obj: JsObject => {
        val id = (obj \ "id").as[String]
        val stats = (obj \ "stats").asOpt[List[Stat]]
        val types = (obj \ "types").asOpt[List[Type]]
        val name = (obj \ "name").asOpt[String]
        val weight = (obj \ "weight").asOpt[Int]
        val sprites = (obj \ "sprites").asOpt[Sprites]
        JsSuccess(Pokemon(id, name, weight, stats, types, sprites))
      }
      case _ => JsError("Data not expected")
    }
  }


  implicit val pokemonWriter: OWrites[Pokemon] = new OWrites[Pokemon] {
    override def writes(p: Pokemon) = Json.obj(
      CF.Id -> p.id,
      CF.Name -> p.name,
      CF.Weight -> p.weight,
      CF.Stats -> p.stats,
      CF.Types -> p.types,
      CF.Sprites -> p.sprites
    )
  }


  def apply(json: JsValue): Pokemon = json match {
    case obj: JsObject => {
      val id = (obj \ "id").as[String]
      val stats = (obj \ "stats").asOpt[List[Stat]]
      val types = (obj \ "types").asOpt[List[Type]]
      val name = (obj \ "name").asOpt[String]
      val weight = (obj \ "weight").asOpt[Int]
      val sprites = (obj \ "sprites").asOpt[Sprites]
      Pokemon(id, name, weight, stats, types, sprites)
    }
    case _ => throw new Exception("Data not expected")
  }
}


final case class Pokemon(override val id: String,
                         name: Option[String],
                         weight: Option[Int],
                         stats: Option[List[Stat]],
                         types: Option[List[Type]],
                         sprites: Option[Sprites],
                         override val dataType: String = "pokemon"
                        ) extends Persistence

final case class SignUp(name: String, password: String)

object SignUp {
  implicit val signFormat = Json.format[SignUp]

  val signInForm = Form(
    mapping(
      CF.Name -> nonEmptyText(2),
      CF.Password -> nonEmptyText(2)
    )(SignUp.apply)(SignUp.unapply)
  )

}

final case class User(override val id: String = generateBSONId,
                      name: String,
                      password: String,
                      override val dataType: String = "user") extends Persistence

object User {
  implicit val userReader: Reads[User] = new Reads[User] {
    override def reads(json: JsValue): JsResult[User] = json match {
      case obj: JsObject => {
        val id = (obj \ "id").as[String]
        val name = (obj \ CF.Name).as[String]
        val password = (obj \ CF.Password).as[String]
        JsSuccess(User(id, name, password))
      }
      case _ => JsError("Data not expected")
    }
  }
  implicit val userWriter: OWrites[User] = new OWrites[User] {
    override def writes(o: User) = Json.obj(
      CF.Id -> o.id,
      CF.Name -> o.name,
      CF.Password -> o.password
    )
  }
}