package controllers

import model.{User, AbstractObject}
import play.api.libs.json.{JsArray, Json, JsObject}
import play.api.mvc.{Result, Action, Controller}
import play.modules.reactivemongo.MongoController

/**
 * Created by fafa on 22/2/15.
 */
abstract class ResourceController extends Controller with MongoController {

  /**
   * Resource object
   */
  val obj:AbstractObject

  /**
   * Method : POST
   * Request Content Type : Json
   * @return
   */
  def create = Action(parse.json) {
    implicit request =>
      val ret_obj = obj.create(request.body.as[JsObject])
      var ret = Json.obj("success"->"","data"->ret_obj)
      Ok(ret)
  }

  /**
   * Method : GET
   * Request Content Type : Json
   *
   * PROTOTYPE: listed all items instead of paginate
   * @return
   */
  def list() = Action{
    implicit request =>
      val page = request.getQueryString("page").getOrElse("1").toInt
      val itemNum = request.getQueryString("itemNum").getOrElse("1000000").toInt
      Ok(Json.obj("data" -> JsArray(obj.list(page,itemNum)),
        "total_num" -> obj.count()))
  }

  /**
   * Method : DELETE
   * Request Content Type : Json
   * @return
   */
  def remove(id:String) = Action{
    implicit request =>
      obj.delete(id)
      Ok("{\"success\":\"\"}")
  }

  /**
   * Method : PUT
   * Request Content Type : Json
   *
   * @return
   */
  def update(id:String) = Action(parse.json){
    implicit request =>
      val request_data = request.body.as[JsObject]
      obj.update(id,request_data)
      Ok("{\"success\":\"\"}")
  }

}
