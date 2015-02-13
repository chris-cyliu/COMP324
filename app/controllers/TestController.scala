package controllers

import com.fasterxml.jackson.databind.node.ObjectNode
import play.api.libs.json.{JsString, Json, JsObject}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import model.User

/**
 * Created by fafa on 11/2/15.
 */
object TestController extends Controller with MongoController{

  val user_collection = db.collection[JSONCollection](User.collection_name)

  def testCreateUser(data:String) = Action{
    val json_object = Json.parse(data).as[JsObject]
    User.create(user_collection, User.setPassword(json_object,(json_object \ User.KW_PASSWORD).as[JsString].value))
    Ok("Success create User")
  }
}
