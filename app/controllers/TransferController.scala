package controllers

import model._
import play.api.libs.json.{JsString, JsArray, Json}
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object TransferController extends ResourceController{
  override val obj: AbstractObject = Transfer

  def getTransferBySerialItem(item_id:String,serial:String) = Action{
    val seq_transfer = Transfer.getTransferRecord(item_id,serial)
    Ok(Json.obj(
      "data" -> JsArray(seq_transfer)
    ))
  }

  def receiveItem(transfer_id:String) = Action{
    implicit request =>
      val json_stringify = Json.stringify(Transfer.get(transfer_id))
      Ok(views.html.layout("Receive Items",views.html.receiveItems(json_stringify),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def approve(transfer_id:String) = Action {
    Transfer.approveTransfer(transfer_id)
    Ok(Json.obj(
      "success"->""
    ))
  }

  def reject(transfer_id:String) = Action {
    Transfer.deniedTransfer(transfer_id)
    Ok(Json.obj(
      "success"->""
    ))
  }
}
