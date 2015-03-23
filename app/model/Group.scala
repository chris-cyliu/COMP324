package model

import play.api.libs.json.{JsString, JsArray, Json}
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONObjectID

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by fafa on 18/2/15.
 */
object Group extends AbstractObject{
  override val collection_name: String = "user_group"

  val KW_NAME = "name"
  val KW_DESCRIBLE = "describe"
  val KW_MEMBER = "member"

  def addUserToGroup(userid:String, groupid:String) ={
    val criteria = Json.obj(
      "_id" -> BSONFormats.toJSON(BSONObjectID.parse(groupid).get)
    )
    val op = Json.obj(
      "$addToSet"->Json.obj{
        KW_MEMBER -> JsString(userid)
      }
    )
    this.collection.update(criteria,op)
  }
}
