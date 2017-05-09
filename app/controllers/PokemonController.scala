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

import controllers.twitter.TwitterInstance
import models.Tweet
import reactivemongo.play.json.collection.JSONCollection
import models.helpers.{MongoDBFields => CF}
import models.helpers.MongoCollection._
import twitter4j.{Query, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

import scala.collection.mutable
import scala.concurrent.duration._
import scala.collection.JavaConverters._
import scala.util.Try

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PokemonController @Inject()(val ws: WSClient, override val reactiveMongoApi: ReactiveMongoApi, override val configuration: play.api.Configuration)
  extends CommonController(reactiveMongoApi, configuration) {


  lazy val mainCollection: Future[JSONCollection] = getJSONCollection(Pokemons)
  override type P = Pokemon
  override implicit val mainReader: Reads[P] = Pokemon.pokemonReader
  override implicit val mainWriter: OWrites[P] = Pokemon.pokemonWriter



  protected lazy val pokemonsName = fillPokemonData.map(_.map(_._1).toList)
  protected val url = configuration.underlying.getString("pokedex.url")
  protected val pokemonUrl = url + "pokemon/"

  def getPokemonData(name: String) = Action.async { implicit request =>
    request.session.get(CF.Id).map { id =>
      getOrInsertPokemon(name).map { pokemon =>
        Ok(Json.toJson(pokemon))
      }.recover {
        case _ => NoContent
      }
    }.getOrElse(Future.successful(Unauthorized("You have to login")))
  }

  def autocomplete(prefix: String) = Action.async { implicit request =>
    request.session.get(CF.Id).map { id =>
      pokemonsName.map { list =>
        Ok(Json.toJson(list.filter(_.toLowerCase.startsWith(prefix.toLowerCase)).sorted.take(5)))
      }.recover {
        case e => NoContent
      }
    }.getOrElse(Future.successful(Unauthorized("You have to login")))
  }

  def getPokemonFromType(name: String, url: String) = Action.async { implicit request =>
    request.session.get(CF.Id).map { id =>
      getAverage(name, url).map { list =>
        Ok(Json.toJson(Average(name, list.toMap)))
      }.recover { case e => NoContent }
    }.getOrElse(Future.successful(Unauthorized("You have to login")))
  }

  def getPokemonTweet(name: String) = Action.async { implicit request =>
    request.session.get(CF.Id).map { id =>
      Future.successful(Ok(Json.toJson(getTweets(name))))
    }.getOrElse(Future.successful(Unauthorized("You have ot login")))
  }

  protected def getOrInsertPokemon(name: String): Future[Pokemon] = {
    findByName(mainCollection)(name).flatMap { po =>
      if (po.isDefined) {
        Future.successful(po.get)
      }
      else {
        createPokemon(name).map { pokemon =>
          insert(mainCollection)(pokemon)
          pokemon
        }
      }
    }
  }

  protected def createPokemon(name: String): Future[Pokemon] = {
    getData(pokemonUrl + name).map { wsr => Pokemon(wsr.json) }
  }

  protected def getTweets(name: String) = {
    Try(TwitterInstance.twitter.search(new Query("#" + name)).getTweets.asScala.map { status =>
      Tweet(status.getUser.getScreenName,status.getText)
    }.toList).getOrElse(Nil)
  }

  protected def getData(url: String) = ws.url(url).withRequestTimeout(300000.millis).get()

  protected def getCount(url: String = pokemonUrl): Future[Int] = {
    ws.url(url).get().map { wsr =>
      wsr.json.validate[PokeData].asOpt.map(_.count).getOrElse(0)
    }
  }

  protected def getAverageFromList(list: List[(String, String)]) = {
    Future.sequence(list.map { case (name, url) => getAverage(name, url) })
  }

  protected def getAverage(name: String, url: String) = {
    getPokemonsFromType(name, url).flatMap { set =>
      val size = set.size
      val list = Future.sequence(set.map { name => getOrInsertPokemon(name) }.toList)
      val res = list.map { pokemons =>
        pokemons.map { pokemon =>
          pokemon.stats.map { stat => (stat.stat.name, stat.base_stat) }
        }.flatten.groupBy(_._1).map { case (name, map) =>
          (name, map.foldLeft(0)((acc, value) => acc + value._2))
        }.mapValues(value => (value / size)).toList
      }
      res
    }
  }


  protected def getPokemonsFromType(name: String, url: String) = {
    val set = mutable.HashSet[String]()
    getData(url).map { wrs =>
      (wrs.json \ "pokemon").validate[List[PokemonType]].asOpt.map { pokemonTypes =>
        pokemonTypes.map { pokemonType =>
          set.add(pokemonType.pokemon.name)
        }
      }
      set
    }
  }

  protected def fillPokemonData: Future[scala.collection.mutable.HashMap[String, String]] = {
    val map = scala.collection.mutable.HashMap[String, String]()

    def fillData(url: String) = {
      getData(url).map { wsr =>
        wsr.json.validate[PokeData].asOpt.map { pokeData =>
          pokeData.results.map { data =>
            map.put(data.name, data.url)
          }
        }
        map
      }
    }

    getCount().flatMap { count =>
      fillData(pokemonUrl + "?limit=" + count)
    }

  }
}
