package controllers

import controllers.UserController._
import model.{User, AbstractObject}
import play.api.libs.json.{JsArray, Json, JsObject}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

/**
 * Created by fafa on 22/2/15.
 */
abstract class ResourceController extends Controller with MongoController {

  /**
   * Resource object
   */
  val obj:AbstractObject

  /**
   * Get mongo collection
   */
  val colleciton:JSONCollection = obj.getCollection(db)

  /**
   * Method : POST
   * Request Content Type : Json
   * @return
   */
  def create = Action(parse.json) {
    implicit request =>
      val ret_obj = obj.create(colleciton,request.body.as[JsObject])
      var ret = Json.obj("success"->"","data"->ret_obj)
      Ok(ret)
  }

  /**
   * Method : GET
   * Request Content Type : Json
   * @return
   */
  def list = Action{
    implicit request =>
      val page = request.getQueryString("page").getOrElse("1").toInt
      val itemNum = request.getQueryString("itemNum").getOrElse("1000000").toInt
      Ok(Json.obj("data" -> JsArray(User.list(colleciton,page,itemNum)),
        "total_num" -> User.count(colleciton)))
  }

  /**
   * Method : DELETE
   * Request Content Type : Json
   * @return
   */
  def remove(id:String) = Action{
    implicit request =>
      obj.delete(colleciton , id)
      Ok("{\"success\":\"\"}")
  }

  /**
   * Method : PUT
   * Request Content Type : Json
   * @return
   */
  def update = ???

}
