package controllers

import model._
import play.api.libs.json
import play.api.libs.json.{JsString, Json, JsArray}
import play.api.mvc.Action

/**
 * Created by Chris on 24/2/15.
 */
object ItemController extends ResourceController {
  override val obj: AbstractObject = Item

  def pageBorrowItem = Action {
    implicit request =>
      Ok(views.html.layout("Borrow Item",views.html.borrowItem(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def pageItemRegistration = Action {
    implicit request =>
       Ok(views.html.layout("Item registration",views.html.itemRegistration(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def pageLocation = Action {
    implicit request =>
      Ok(views.html.layout("Manage Location",views.html.locationManagement(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def itemManage = Action {
    implicit request =>
      Ok(views.html.layout("Item Management",views.html.itemManagement(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def pageAssignItem = Action {
    implicit request =>
      Ok(views.html.layout("Assign item",views.html.assignItem(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def addSerial = Action(parse.json){
    implicit  request =>
      val serialItemList = request.body.as[JsArray].value
      serialItemList.foreach(Item.addSerial(_))

      val to_location_id = (serialItemList(0) \ "location_id").as[JsString].value
      val item_id_serial_seq = serialItemList.map({x=>
        val item_id = (x \ "item_id").as[JsString].value
        val serial = (x \ "serial").as[JsString].value
        (item_id,serial)
      })
      //add transfer record
      Transfer.assignItem(to_location_id, item_id_serial_seq);
      Ok(Json.obj("success"->""))
  }

  def getSerialByIdLocation(item_id:String,location_id:String) = Action {
    implicit request =>
      val data = Item.getSerial(item_id,location_id)
      Ok(Json.obj(
        "data" -> JsArray(data)
      ))
  }

  def aduit(location_id:String) = Action {
    implicit request =>
      val data = Item.aduit(location_id)
      Ok(Json.obj(
        "data" -> JsArray(data)
      ))
  }
  def pageAduit = Action {
    implicit request =>
      Ok(views.html.layout("Aduit items",views.html.aduitPage(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }
}
