package controllers

import javax.inject.Singleton

import controllers.mongo.MongoCRUD
import play.api.mvc.Controller
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}
/**
  * Created by wong on 02/05/17.
  */
@Singleton
abstract class CommonController (val reactiveMongoApi: ReactiveMongoApi, val configuration: play.api.Configuration)
  extends Controller with MongoController with ReactiveMongoComponents with MongoCRUD
