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

  def transfer(from_location_id:Option[String], to_location_id:String, item_id:String, serial:String,status:String) = {
    val data = Json.obj(
      KW_FROM -> (from_location_id match{
        case Some(e) =>
          JsString(e)
        case None =>
          JsNull
      }),
      KW_TO -> JsString(to_location_id),
      KW_ITEMS -> JsArray(
        Json.obj(
          KW_ITEMS_ID -> item_id,
          KW_ITEMS_SERIAL -> serial
        )::Nil
      ),
      KW_STATUS ->status
    )

    val location_obj = Location.get(to_location_id)
    val to_user_ids = (location_obj \ Location.KW_PIC).as[JsArray].value.map(_.as[JsString].value)

    //Create transfer record
    val ret_transfer_obj = this.create(data)
    val ret_transfer_obj_id = (ret_transfer_obj \ KW_ID).as[JsString].value

    //get common link and attach in message
    val base_url = common.Util.basePath
    val transfer_url = common.Util.basePath + "/transfer/"+ ret_transfer_obj_id
    //CREATE message
    to_user_ids.foreach({user_id =>
      Message.create(Json.obj(
        Message.KW_FROM -> JsNull,
        Message.KW_TO -> JsString(user_id) ,
        Message.KW_MSG -> JsString("A new transfer is issued. Please refer to the link: <a href=\""+transfer_url+"\">"+transfer_url+"</a>")
      ))
    })

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
