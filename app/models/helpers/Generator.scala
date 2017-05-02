package models.helpers

import reactivemongo.bson.BSONObjectID

/**
  * Created by wong on 02/05/17.
  */
object Generator {

  def generateBSONId = BSONObjectID.generate().stringify

}
