package controllers

import model.{Transfer, Item, AbstractObject}
import play.api.libs.json
import play.api.libs.json.{JsString, Json, JsArray}
import play.api.mvc.Action

/**
 * Created by Chris on 24/2/15.
 */
object ItemController extends ResourceController {
  override val obj: AbstractObject = Item

  def pageBorrowItem = Action {
    Ok(views.html.layout("Borrow Item",views.html.borrowItem()))
  }

  def pageItemRegistration = Action {
    Ok(views.html.layout("Item registration",views.html.itemRegistration()))
  }

  def pageLocation = Action {
    Ok(views.html.layout("Manage Location",views.html.locationManagement()))
  }

  def itemManage = Action {
    Ok(views.html.layout("Item Management",views.html.itemManagement()))
  }

  def pageAssignItem = Action {
    Ok(views.html.layout("Assign item",views.html.assignItem()))
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
    Ok(views.html.layout("Aduit items",views.html.aduitPage()))
  }
}
