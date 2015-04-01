package controllers

import common.ArrayQueryParam
import model.{ AbstractObject}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
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
      Ok(Json.obj{
        "data"->ret
      })
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
      Ok(Json.obj("data" -> JsArray(obj.list(0,Int.MaxValue))))
  }

  /**
   * Action for datatable
   * @return
   */
  //TODO: handle datatable search function
  def listDataTable() = Action{
    implicit request =>

      val draw = request.getQueryString("draw").getOrElse(throw new Exception("Missing parameter \"draw\"")).toInt
      val start = request.getQueryString("start").getOrElse(throw new Exception("Missing parameter \"start\"")).toInt
      val length= request.getQueryString("length").getOrElse(throw new Exception("Missing parameter \"length\"")).toInt

      val search = ArrayQueryParam("search",request.queryString)
      val order = ArrayQueryParam("order",request.queryString)
      val column = ArrayQueryParam("columns",request.queryString)

      var totalCount = 0//obj.count()

      val data = obj.list(start,length)

      val ret = Json.obj(
        "draw" -> draw,
        "recordTotal" ->totalCount,
        "recordsFiltered" -> totalCount,
        "data" -> JsArray(data)
      )
      Ok(ret)
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

  /**
   * [{id: , objid ,type},{id: ,type:, objid},{id: , type: , objid}]
   * @return
   */
  def updateACL() = Action(parse.json){
    implicit request =>
      val array_pair = request.body.as[JsArray]
      array_pair.value.foreach({
        //foreach add to obj
        a:JsValue =>
          obj.updateAcl((a\"id").as[JsString].value,(a\"act_id").as[JsString].value,(a\"level").as[JsNumber].value.toInt,(a\"type").as[JsString].value)
      })
      Ok(Json.obj(
        "success" -> JsString("")
      ))
  }

  def getByIds = Action(parse.json){
    implicit request =>
      val array_json_ids = request.body.as[JsArray].value
      val ids_string = array_json_ids.map({x=>
        x.as[JsString].value
      })
      val objs = obj.getListByIds(ids_string)
      Ok(
        Json.obj(
          "data" -> JsArray(objs)
        )
      )
  }
}
