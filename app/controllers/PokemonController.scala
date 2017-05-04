package controllers

import javax.inject._

import models.pokemons._
import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.modules.reactivemongo.{MongoController, ReactiveMongoApi, ReactiveMongoComponents}

import scala.collection.mutable._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton

import reactivemongo.play.json.collection.JSONCollection

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PokemonController @Inject()(val ws: WSClient, override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi,configuration) {


  lazy val mainCollection: Future[JSONCollection] = getJSONCollection("pokemons")
  override type P = Pokemon
  override implicit val mainReader: Reads[P] = Pokemon.pokemonReader
  override implicit val mainWriter: OWrites[P] = Pokemon.pokemonWriter


  protected lazy val pokemons: Future[scala.collection.mutable.HashMap[String, String]] = fillPokemonData
  protected lazy val pokemonsName = pokemons.map(_.map(_._1).toList)
  protected lazy val pokemonsData = HashMap[String,Future[Pokemon]]()
  protected val url = configuration.underlying.getString("pokedex.url")
  protected val pokemonUrl = url + "pokemon/"

  def getPokemonData(name: String) = Action.async { implicit request =>
    /*pokemonsData.getOrElseUpdate(name, createPokemon(name)).map { pokemon =>
      Ok(Json.toJson(pokemon))
    }*/
    getOrInsertPokemon(name).map { pokemon =>
      println(pokemon)
      Ok(Json.toJson(pokemon))
    }.recover {
      case _ => NoContent
    }
  }

  def getPokedex = Action.async { implicit request =>
    Future.successful(Ok(views.html.pokedex()))
  }

  protected def getOrInsertPokemon(name: String): Future[Pokemon] = {
    findByName(mainCollection)(name).flatMap { po =>
      if(po.isDefined) {
        println(po)
        Future.successful(po.get)
      }
      else {
        createPokemon(name).map { pokemon =>
          println(pokemon)
          insertPokemonInDB(pokemon)
          pokemon
        }
      }
    }
  }

  protected def insertPokemonInDB(pokemon: Pokemon) = {
    insert(mainCollection)(pokemon)
  }


  protected def createPokemon(name: String): Future[Pokemon] = {
    getData(pokemonUrl+name).map { wsr => Pokemon(wsr.json) }
  }

  protected def getData(url: String) = ws.url(url).get()


  protected def fillPokemonData: Future[scala.collection.mutable.HashMap[String, String]] = {
    val map = scala.collection.mutable.HashMap[String, String]()

    def getCount(url: String = pokemonUrl): Future[Int] = {
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

    getCount().flatMap { count =>
      fillData(pokemonUrl + "?limit=" + count)
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
