package controllers

import javax.inject.Inject

import models.pokemons.User
import play.api.libs.json.{OWrites, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import javax.inject.Singleton

import reactivemongo.play.json.collection.JSONCollection
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by wong on 02/05/17.
  */
@Singleton
class UserController @Inject()(override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi,configuration) {


  val mainCollection: Future[JSONCollection] = getJSONCollection("users")
  override type P = User
  override implicit val mainReader: Reads[P] = User.userReader
  override implicit val mainWriter: OWrites[P] = User.userWriter
}
