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
import models.helpers.MongoDBFields.Id
import models.helpers.Generator._
import org.mindrot.jbcrypt.BCrypt

/**
  * Created by wong on 02/05/17.
  */
@Singleton
class UserController @Inject()(override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi,configuration) {

  private final val salt = BCrypt.gensalt()

  val mainCollection: Future[JSONCollection] = getJSONCollection("users")
  override type P = User
  override implicit val mainReader: Reads[P] = User.userReader
  override implicit val mainWriter: OWrites[P] = User.userWriter

  def index = Action { implicit request =>
    request.session.get(Id).map { id =>
      Ok(views.html.dashboard())
    }.getOrElse(Ok(views.html.login()))
  }

  def signUp = Action.async { implicit request =>
    SignUp.signInForm.bindFromRequest().fold(
      hasErrors => getJsonFormError[SignUp](hasErrors),
      signUp => {
        findByName(mainCollection)(signUp.name).map { ou =>
          if(ou.isDefined) {
            Redirect(routes.UserController.index).withSession(Id -> ou.get.id)
          }
          else {
            val user = User(name = signUp.name, password = BCrypt.hashpw(signUp.password,salt))
            insert(mainCollection)(user)
            Redirect(routes.UserController.index).withSession(Id -> user.id)
          }
        }
      }
    )
  }

}
