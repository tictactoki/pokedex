
package controllers.mongo

import controllers.CommonController
import models.pokemons.Persistence
import play.api.data.Form
import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import play.api.mvc.{Action, Result}
import play.modules.reactivemongo.json._
import reactivemongo.api.commands.WriteResult
import reactivemongo.play.json.collection.JSONCollection
import models.helpers.MongoDBFields._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by wong on 02/05/17.
  */
trait MongoCRUD { self: CommonController =>

  type P <: Persistence

  //implicit val mainCollection: Future[JSONCollection]
  implicit val mainReader: Reads[P]
  implicit val mainWriter: OWrites[P]

  implicit protected val fieldQuery = (field: String, value: String) => Json.obj(field -> value)

  protected def getJSONCollection(name: String) = reactiveMongoApi.database.map {_.collection[JSONCollection](name)}

  protected def findByName(mainCollection: Future[JSONCollection])(name: String): Future[Option[P]] = {
    for {
      collection <- mainCollection
      list <- collection.find(fieldQuery(Name,name)).cursor[P]().collect[List]()
    } yield {
      list.headOption
    }
  }

  protected def delete(mainCollection: Future[JSONCollection])(field: String, value: String) = {
    mainCollection.flatMap(_.remove(Json.obj(field -> value)))
  }

  protected def insert(mainCollection: Future[JSONCollection])(obj: P): Future[WriteResult] = {
    mainCollection.flatMap(_.insert(obj))
  }

  protected def update(mainCollection: Future[JSONCollection])(obj: P): Future[WriteResult] = {
    mainCollection.flatMap(_.update(fieldQuery(Id,obj.id),obj))
  }


  protected def findByField(mainCollection: Future[JSONCollection])(field: String, value: String) = {
    for {
      collection <- mainCollection
      list <- collection.find(fieldQuery(field,value)).cursor[P]().collect[List]()
    } yield {
      list.headOption
    }
  }

  protected def getJsonFormError[T](form: Form[T]): Future[Result] = {
    val jsError = form.errors.foldLeft(Json.obj())((acc, js) => acc ++ Json.obj(js.key -> js.message))
    Future.successful(BadRequest(jsError))
  }

}