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

  def index = Action.async { implicit request =>
    request.session.get(Id).map { id =>
      Future.successful(Ok(views.html.dashboard()))
    }.getOrElse(Future.successful(Ok(views.html.login(SignUp.signInForm))))
  }

  def signUp = Action.async { implicit request =>
    SignUp.signInForm.bindFromRequest().fold(
      hasErrors => {
        println(hasErrors)
        getJsonFormError[SignUp](hasErrors)
      },
      signUp => {
        println(signUp)
        findByName(mainCollection)(signUp.name).map { ou =>
          if(ou.isDefined) {
            val validPass = BCrypt.checkpw(signUp.password, ou.get.password)
            if(validPass) Redirect(routes.UserController.index).withSession(Id -> ou.get.id)
            else Redirect(routes.UserController.index)
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
