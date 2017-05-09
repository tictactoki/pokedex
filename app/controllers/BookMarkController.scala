package controllers

import javax.inject.{Inject, Singleton}

import models.helpers.Generator._
import models.helpers.MongoCollection._
import models.helpers.MongoDBFields._
import models.persistences.{Book, Bookmark}
import play.api.libs.json.{Json, OWrites, Reads}
import play.api.mvc.Action
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by wong on 08/05/17.
  */
@Singleton
class BookMarkController @Inject()(override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi,configuration) {
  lazy val mainCollection: Future[JSONCollection] = getJSONCollection(Bookmarked)
  override type P = Bookmark
  override implicit val mainReader: Reads[P] = Bookmark.bookmarkReader
  override implicit val mainWriter: OWrites[P] = Bookmark.bookmarkWriter

  def getBookmarks = Action.async { implicit request =>
    request.session.get(Name).map { name =>
      find(mainCollection)(Name,name).map { bookmarks =>
        Ok(Json.toJson(bookmarks.map(_.pokemon).toSet))
      }.recover { case e => Conflict(e.getMessage) }
    }.getOrElse(Future.successful(Unauthorized("Need to log in")))
  }

  def bookmarkPokemon(name: String) = Action.async { implicit request =>
    request.session.get(Name).map { n =>
      insert(mainCollection)(Bookmark(generateBSONId,n,name)).map { res =>
        if(res.ok) Ok("")
        else Conflict("")
      }.recover{case e => BadRequest(e.getMessage)}
    }.getOrElse(Future.successful(Unauthorized("Need to login")))
  }

  def removeBookmark(name: String) = Action.async { implicit request =>
    request.session.get(Name).map { n =>
      mainCollection.flatMap(_.remove(Book(name,n))).map { res =>
        if(res.ok) Ok("")
        else Conflict("")
      }.recover { case e => BadRequest(e.getMessage)}
    }.getOrElse(Future.successful(Unauthorized("Need to login")))
  }

}
