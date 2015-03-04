package model

import common.Util
import play.api.libs.json._

/**
 * Transfer log
 * {
 *    from: location_id
 *    to: location_id
 *    items:{
 *      item_id:
 *      serial:
 *    }
 *    status: //pending,approved,denied
 * }
 */
object Transfer extends AbstractObject {
  override val collection_name: String = "transfer"

  val KW_FROM = "from"

  val KW_TO = "to"

  val KW_ITEMS = "items"

  val KW_ITEMS_ID = "item_id"

  val KW_ITEMS_SERIAL = "serial"

  val KW_STATUS = "status"

  val KW_PENDING = "pending"

  val KW_APPROVED = "approved"

  val KW_DENIED = "denied"

  def transfer(location_id:String, item_id:String, serial:String,status:String) = {
    val data = Json.obj(
      KW_FROM ->JsNull,
      KW_TO -> JsString(location_id),
      KW_ITEMS -> JsArray(
        Json.obj(
          KW_ITEMS_ID -> item_id,
          KW_ITEMS_SERIAL -> serial
        )::Nil
      ),
      KW_STATUS ->status
    )

    this.create(data)
  }

  /**
   * Approved trasnfer
   * - change status
   * - change item->serial->location
   * @param id
   */
  def approveTransfer(id:String) = {
    val transfer:JsValue = this.get(id)

    //update status
    this.update(id ,Util.Mongo.setField(KW_STATUS,KW_APPROVED))

    val item_ids_serial = (transfer \ Transfer.KW_ITEMS).as[JsArray].value.map(
      x=>
        (((x \ KW_ITEMS) \KW_ITEMS_ID).as[JsString].value,
          ((x \ KW_ITEMS) \KW_ITEMS_SERIAL).as[JsString].value
        )
    )
    val location_id = (transfer \ Transfer.KW_TO).as[JsString].value
    item_ids_serial.foreach({
      a =>
        Item.update(a._1 , Util.Mongo.setField(KW_ITEMS+"."+a._2,location_id))
    })
  }

  def deniedTransfer(id:String) = {
    this.update(id ,Util.Mongo.setField(KW_STATUS,KW_DENIED))
  }
}
