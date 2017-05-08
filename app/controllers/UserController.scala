package controllers

import javax.inject.Inject

import models.pokemons.{SignUp, User}
import play.api.libs.json.{OWrites, Reads}
import play.modules.reactivemongo.ReactiveMongoApi
import javax.inject.Singleton

import play.api.mvc.{Action, Session}
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import models.helpers.MongoDBFields._
import models.helpers.Generator._
import org.mindrot.jbcrypt.BCrypt
import models.helpers.MongoCollection._
/**
  * Created by wong on 02/05/17.
  */
@Singleton
class UserController @Inject()(override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi,configuration) {

  private final val salt = BCrypt.gensalt()

  lazy val mainCollection: Future[JSONCollection] = getJSONCollection(Users)
  override type P = User
  override implicit val mainReader: Reads[P] = User.userReader
  override implicit val mainWriter: OWrites[P] = User.userWriter

  def index = Action.async { implicit request =>
    request.session.get(Id).map { id =>
      Future.successful(Ok(views.html.dashboard()))
    }.getOrElse(Future.successful(Ok(views.html.login(SignUp.signInForm))))
  }

  def logout = Action { implicit request =>
    Redirect(routes.UserController.index()).withNewSession
  }

  def signUp = Action.async { implicit request =>
    SignUp.signInForm.bindFromRequest().fold(
      hasErrors => getJsonFormError[SignUp](hasErrors),
      signUp => {
        findByName(mainCollection)(signUp.name).flatMap { ou =>
          if(ou.isDefined) {
            val validPass = BCrypt.checkpw(signUp.password, ou.get.password)
            if(validPass) Future.successful(Redirect(routes.UserController.index).withSession(Id -> ou.get.id, Name -> signUp.name))
            else Future.successful(Redirect(routes.UserController.index))
          }
          else {
            val user = User(name = signUp.name, password = BCrypt.hashpw(signUp.password,salt))
            insert(mainCollection)(user).map { wr =>
              if(wr.ok) Redirect(routes.UserController.index).withSession(Id -> user.id, Name -> signUp.name)
              else Redirect(routes.UserController.index)
            }
          }
        }
      }
    )
  }

}
