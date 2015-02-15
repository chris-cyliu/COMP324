package model

import error.MongodbException
import play.api.libs.Crypto
import play.api.libs.json.{JsString, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import reactivemongo.api.QueryOpts

/**
 * User object
 */
object User{
  val KW_USERNAME = "name"
  val KW_PASSWORD = "pw"
  val KW_ID = "ObjectId"

  val collection_name = "user"

  def getUserName(in:JsObject):String = {
    in \ User.KW_USERNAME match{
      case a:JsString =>
        a.value
      case _ =>
        throw new Exception("Missing key \"$User.KW_USERNAME\"")
    }
  }

  def getPassword(in:JsObject):String = {
    Crypto.decryptAES(
      in \ User.KW_PASSWORD match {
        case a:JsString =>
          a.value
        case _ =>
          throw new Exception("Missing key \"$User.KW_PASSWORD\"")
      })
  }

  def setPassword(in:JsObject, text:String):JsObject = {
    in - User.KW_PASSWORD + (User.KW_PASSWORD -> JsString(Crypto.encryptAES(text)))
  }

  def create(collection:JSONCollection , in:JsObject) = {
    collection.insert(in).map(err =>
      throw new MongodbException(err.toString)
    )
  }

  def list(collection:JSONCollection , page:Int, item_per_page:Int , offset:Int) = {
    val query = collection.find().options(QueryOpts((page-1)*item_per_page,item_per_page)).sort()
    //filter password

  }
}