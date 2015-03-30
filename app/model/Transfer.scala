package model

import java.util.{Calendar, TimeZone}

import common.Util
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONDateTime

/**
 * Transfer log
 * {
 *    from: location_id
 *    to: location_id
 *    type : // may be "assign" / "borrow"
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

  val KW_TYPE = "type"

  val KW_TIMESTAMP = "timestamp"

  def assignItem(to_location_id:String, item_id_serial_seq:Seq[(String,String)]): Unit ={
    val items_json = item_id_serial_seq.map({x=>
      Json.obj(
        KW_ITEMS_ID -> x._1,
        KW_ITEMS_SERIAL -> x._2
      )
    })
    val transfer = Json.obj(
      KW_TO -> to_location_id,
      KW_ITEMS -> JsArray(items_json),
      KW_STATUS -> KW_APPROVED
    )
    this.create(transfer)
  }

  override def create(transfer_json:JsValue) = {

    val location_obj = Location.get((transfer_json \ KW_TO).as[JsString].value)
    val to_user_ids = (location_obj \ Location.KW_PIC).as[JsArray].value.map(_.as[JsString].value)

    //Create transfer record
    val temp = transfer_json.as[JsObject] + (KW_TIMESTAMP -> BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())))
    val ret_transfer_obj = super.create(temp)
    val ret_transfer_obj_id = (ret_transfer_obj \ KW_ID \"$oid").as[JsString].value

    //get common link and attach in message
    val base_url = common.Util.basePath
    val transfer_url = common.Util.basePath + "/transfer/"+ ret_transfer_obj_id
    //CREATE message
    to_user_ids.foreach({user_id =>
      Message.create(Json.obj(
        Message.KW_FROM -> JsNull,
        Message.KW_TO -> JsString(user_id) ,
        Message.KW_TITLE -> "Transfer request",
        Message.KW_MSG -> JsString("A new transfer is issued. Please refer to the link: <a href=\""+transfer_url+"\">"+transfer_url+"</a>")
      ))
    })

    ret_transfer_obj
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
    this.update(id ,Json.obj(KW_STATUS->KW_APPROVED))

    //get turple of item id an serial
    val item_ids_serial = (transfer \ Transfer.KW_ITEMS).as[JsArray].value.map(
      x=>
        (
          (x \ KW_ITEMS_ID).as[JsString].value,
          (x \ KW_ITEMS_SERIAL).as[JsString].value
        )
    )

    val location_id = (transfer \ Transfer.KW_TO).as[JsString].value
    val transfer_type = (transfer \ KW_TYPE).as[JsString].value

    val new_serial = item_ids_serial.foreach({
      a =>
        //get item node
        val item_obj = Item.get(a._1).as[JsObject]

        //search serial and update the location accourding transfer type
        val serial = (item_obj \ "serial").as[JsArray].value.map({x=>
          (x \ "serial").as[JsString].value match{
            case a._2 =>
              transfer_type match {
                case "assign" =>
                  x.as[JsObject] ++ Json.obj("cur_location"->location_id) ++ Json.obj("own_location"->location_id)
                case "borrow" =>
                  x.as[JsObject] ++ Json.obj("cur_location"->location_id)
              }
            case _ => x
          }
        })

        //update item
        Item.update((item_obj \"_id"\"$oid").as[JsString].value,Json.obj("serial" -> JsArray(serial)))

    })
  }

  def deniedTransfer(id:String) = {
    this.update(id ,Json.obj(KW_STATUS -> KW_DENIED))
  }

  def getTransferRecord(item_id:String,serial:String) = {
    val selector = Json.obj(
      KW_ITEMS -> Json.obj(
        "$elemMatch" -> Json.obj(
          KW_ITEMS_ID -> item_id,
          KW_ITEMS_SERIAL -> serial
        )
      ),
      KW_STATUS -> KW_APPROVED
    )
    this.list(0,Int.MaxValue)(selector)
  }
}
