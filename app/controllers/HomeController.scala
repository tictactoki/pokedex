package controllers

import javax.inject._

import models.pokemons.PokeData
import play.api._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val ws: WSClient,configuration: play.api.Configuration) extends Controller {



  protected lazy val pokemons = fillPokemonData
  protected lazy val poke = pokemons.map(_._1)
  protected val url = configuration.underlying.getString("pokedex.url")


  def getAllPokemonName = Action.async { implicit request =>
    val f = ws.url(url + "pokemon").get()
    f.map{ ws =>
      val test = ws.json.validate[PokeData]
      val t = test.asOpt.map { pokeData =>
        pokeData
      }.getOrElse(throw new Exception("data not found"))
      Ok(Json.toJson(t))
    }
  }

  protected def fillPokemonData: scala.collection.mutable.HashMap[String,String] = {
    val map = scala.collection.mutable.HashMap[String,String]()

    def fillData(url: String): Unit = {
      ws.url(url).get().map { wsr =>
        wsr.json.validate[PokeData].asOpt.map { pokeData =>
          pokeData.results.map { data => map.put(data.name,data.url)}
          pokeData.next.map { next => fillData(next)}
        }
      }
    }
    map
  }

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }
}
