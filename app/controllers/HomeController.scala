package controllers

import javax.inject._

import models.pokemons._
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(val ws: WSClient, configuration: play.api.Configuration) extends Controller {


  protected lazy val pokemons: Future[scala.collection.mutable.HashMap[String, String]] = fillPokemonData
  protected lazy val poke = pokemons.map(_.map(_._1).toList)
  protected val url = configuration.underlying.getString("pokedex.url")
  protected val pokemonUrl = url + "pokemon/"
  protected val pokemonFormUrl = url + "pokemon-form/"


  def getAllPokemonName = Action.async { implicit request =>
    /*val f = ws.url(url + "pokemon").get()
    f.map{ ws =>
      val test = ws.json.validate[PokeData]
      val t = test.asOpt.map { pokeData =>
        pokeData
      }.getOrElse(throw new Exception("data not found"))*/
    poke.flatMap { l =>
      getData(pokemonUrl + l(0)).map { wsr =>
        println(wsr.json)
        val pokemon = wsr.json match {
          case obj: JsObject => {
            val stats = (obj \ "stats").asOpt[List[Stat]]
            val types = (obj \ "types").asOpt[List[Type]]
            val id = (obj \ "id").asOpt[Int]
            val name = (obj \ "name").asOpt[String]
            val weight = (obj \ "weight").asOpt[Int]
            val sprites = (obj \ "sprites").asOpt[Sprites]
            Pokemon(id, name, weight, stats, types, sprites)
          }
            case _ => throw new Exception("Data not expected")
          }
        Ok(Json.toJson(pokemon))
        }
        /*val pokemonForm = wsr.json.validate[PokemonForm].asOpt.getOrElse(throw new Exception("data not found"))
        Ok(Json.toJson(pokemonForm))*/
      }
    }

    //poke.map(i => Ok(Json.toJson(i)))
    //Future.successful(Ok(Json.toJson(poke)))


  protected def getData(url: String) = ws.url(url).get()


  protected def fillPokemonData: Future[scala.collection.mutable.HashMap[String, String]] = {
    val map = scala.collection.mutable.HashMap[String, String]()

    def getCount(url: String): Future[Int] = {
      ws.url(url).get().map { wsr =>
        wsr.json.validate[PokeData].asOpt.map(_.count).getOrElse(0)
      }
    }

    def fillData(url: String) = {
      ws.url(url).get().map { wsr =>
        wsr.json.validate[PokeData].asOpt.map { pokeData =>
          pokeData.results.map { data => map.put(data.name, data.url) }
        }
        map
      }
    }

    getCount(pokemonUrl).flatMap { count =>
      fillData(url + "pokemon/?limit=" + count)
    }

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
