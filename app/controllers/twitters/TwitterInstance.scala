package controllers.twitters

import twitter4j.{Twitter, TwitterFactory}
import twitter4j.conf.ConfigurationBuilder

/**
  * Created by wong on 09/05/17.
  */
trait TwitterInstance {

  protected val twitter: Twitter

  protected def getTwitterInstance(configurationBuilder: ConfigurationBuilder) = {
    new TwitterFactory(configurationBuilder.build).getInstance()
  }



}
