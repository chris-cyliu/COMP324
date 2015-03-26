package controllers

import model.{Item, AbstractObject}
import play.api.libs.json
import play.api.libs.json.{Json, JsArray}
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

  def pageReceiveItem = Action {
    Ok(views.html.layout("Receive Item",views.html.receiveItems()))
  }

  def pageAssignItem = Action {
    Ok(views.html.layout("Assign item",views.html.assignItem()))
  }

  def addSerial = Action(parse.json){
    implicit  request =>
      val serialItemList = request.body.as[JsArray].value
      serialItemList.foreach(Item.addSerial(_))
      Ok(Json.obj("success"->""))
  }
}
