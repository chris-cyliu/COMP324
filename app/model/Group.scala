package model

import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONObjectID

import scala.util.parsing.json.JSONFormat

/**
 * Created by fafa on 18/2/15.
 */
object Group extends AbstractObject{
  override val collection_name: String = "group"

  val KW_NAME = "name"
  val KW_DESCRIBLE = "describe"
  val KW_MEMBER = "member"

  def getSystemGroup(): Unit = {

  }

  def getGroupsByUserid(id:String) = {
    val id_obj = BSONFormats.toJSON(BSONObjectID.apply(id))


  }
}
