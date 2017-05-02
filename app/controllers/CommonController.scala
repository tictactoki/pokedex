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
  extends Controller with MongoController with ReactiveMongoComponents with MongoCRUD
