package controllers.twitters

import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

/**
  * Created by wong on 09/05/17.
  */
object TwitterInstance {

  final lazy val twitter = new TwitterFactory(initConfig.build).getInstance()

  protected def initConfig = {
    new ConfigurationBuilder()
  }

}
