package models

import play.api.libs.json.Json

/**
  * Created by wong on 09/05/17.
  */
case class Tweet(user: String, text: String)

object Tweet {
  implicit val tweetFormat = Json.format[Tweet]
}