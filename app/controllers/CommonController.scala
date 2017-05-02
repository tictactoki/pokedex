package controllers

import models.pokemons.Persistence
import play.api.mvc.Controller
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.play.json.collection.JSONCollection
import models.helpers.MongoDBFields._
import javax.inject.Singleton

import play.api.libs.json.{JsObject, Json, OWrites, Reads}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import Persistence._
import play.modules.reactivemongo.json._
import ImplicitBSONHandlers._
import controllers.mongo.MongoCRUD
import reactivemongo.play.json._
/**
  * Created by wong on 02/05/17.
  */
@Singleton
abstract class CommonController (val reactiveMongoApi: ReactiveMongoApi, val configuration: play.api.Configuration)
  extends Controller with MongoController with ReactiveMongoComponents with MongoCRUD {

  /*type T <: Persistence

  implicit val mainReader: Reads[T]
  implicit val mainWriter: OWrites[T]

  protected val fieldQuery = (field: String, value: String) => Json.obj(field -> value)

  protected def getCollection(name: String) = reactiveMongoApi.database.map(_.collection[JSONCollection](name))

  protected def insert(collection: Future[JSONCollection])(obj: T) = collection.flatMap(_.insert(obj))

  protected def findById(collection: Future[JSONCollection])(id: String) = {
    for {
      col <- collection
      list <- col.find(fieldQuery(Id,id)).cursor[T]().collect[List]()
    } yield list.headOption
  }

  protected def update(collection: Future[JSONCollection])(obj: T) = {
    collection.flatMap(_.update(fieldQuery(Id,obj.id),obj))
  }

  protected def findByField(collection: Future[JSONCollection])(field: String, value: String) = {
    for {
      col <- collection
      list <- col.find(fieldQuery(field,value)).cursor[T]().collect[List]()
    } yield list
  }*/

}
