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
          case DataType.BookMark => Bookmark.bookmarkReader.reads(json)
        }
      }
      case _ => throw new Exception("No json object matched")
    }
  }

  implicit val persistenceWriter: OWrites[Persistence] = new OWrites[Persistence] {
    override def writes(o: Persistence): JsObject = o match {
      case p: Pokemon => Pokemon.pokemonWriter.writes(p)
      case u: User => User.userWriter.writes(u)
      case b: Bookmark => Bookmark.bookmarkWriter.writes(b)
      case _ => throw new Exception("No writer defined")
    }
  }

}

case class Book(name: String, pokemon: String)
object Book {
  implicit val bookFormat = Json.format[Book]
}

final case class Bookmark(override val id: String,
                          name: String,
                          pokemon: String,
                          dataType: String = "bookmark"
                         ) extends Persistence

object Bookmark {
  implicit val bookmarkReader: Reads[Bookmark] = new Reads[Bookmark] {
    override def reads(json: JsValue) = json match {
      case obj: JsObject => {
        val id = (obj \ CF.Id).as[String]
        val name = (obj \ CF.Name).as[String]
        val pokemon = (obj \ CF.Pokemon).as[String]
        JsSuccess(Bookmark(id, name, pokemon))
      }
      case _ => JsError("Data not expected")
    }
  }

  implicit val bookmarkWriter: OWrites[Bookmark] = new OWrites[Bookmark] {
    override def writes(o: Bookmark) = Json.obj(
      //CF.Id -> generateBSONId,
      CF.Name -> o.name,
      CF.Pokemon -> o.pokemon
    )
  }
}

object Pokemon {
  implicit val pokemonReader: Reads[Pokemon] = new Reads[Pokemon] {
    override def reads(json: JsValue) = json match {
      case obj: JsObject => {
        val id = (obj \ CF.Id).as[String]
        val stats = (obj \ CF.Stats).as[List[Stat]]
        val types = (obj \ CF.Types).as[List[Type]]
        val name = (obj \ CF.Name).asOpt[String]
        val weight = (obj \ CF.Weight).asOpt[Int]
        val sprites = (obj \ CF.Sprites).asOpt[Sprites]
        val height = (obj \ CF.Height).asOpt[Int]
        JsSuccess(Pokemon(id, name, weight, height, stats, types, sprites))
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
      CF.Sprites -> p.sprites,
      CF.Height -> p.height
    )
  }


  def apply(json: JsValue): Pokemon = json match {
    case obj: JsObject => {
      val stats = (obj \ CF.Stats).as[List[Stat]]
      val types = (obj \ CF.Types).as[List[Type]]
      val name = (obj \ CF.Name).asOpt[String]
      val weight = (obj \ CF.Weight).asOpt[Int]
      val sprites = (obj \ CF.Sprites).asOpt[Sprites]
      val height = (obj \ CF.Height).asOpt[Int]
      Pokemon(generateBSONId, name, weight, height, stats, types, sprites)
    }
    case _ => throw new Exception("Data not expected")
  }
}


final case class Pokemon(override val id: String,
                         name: Option[String],
                         weight: Option[Int],
                         height: Option[Int],
                         stats: List[Stat],
                         types: List[Type],
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
        val id = (obj \ CF.Id).as[String]
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