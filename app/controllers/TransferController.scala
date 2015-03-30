package controllers

import model.{Transfer, AbstractObject, Feature}
import play.api.libs.json.{JsArray, Json}
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
    val json_stringify = Json.stringify(Transfer.get(transfer_id))
    Ok(views.html.layout("Receive Items",views.html.receiveItems(json_stringify)))
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
