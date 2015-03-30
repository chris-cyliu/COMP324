package model

import java.util.{Calendar, TimeZone}

import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONDateTime

/**
 * Created by fafa on 29/3/15.
 */
object Borrow extends AbstractObject{
  override val collection_name: String = "borrow"

  val KW_FROM = "from"

  val KW_TO = "to"

  val KW_ITEMS = "items"

  val KW_ITEMS_ID = "item_id"

  val KW_ITEMS_QTY = "qty"

  val KW_STATUS = "status"

  val KW_PENDING = "pending"

  val KW_APPROVED = "approved"

  val KW_DENIED = "denied"

  val KW_TYPE = "type"

  val KW_TIMESTAMP = "timestamp"

  override def create(transfer_json:JsValue) = {

    val location_obj = Location.get((transfer_json \ KW_FROM).as[JsString].value)
    val to_user_ids = (location_obj \ Location.KW_PIC).as[JsArray].value.map(_.as[JsString].value)

    //Create transfer record
    val temp = transfer_json.as[JsObject] + (KW_TIMESTAMP -> BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())))
    val ret_transfer_obj = super.create(temp)
    val ret_transfer_obj_id = (ret_transfer_obj \ KW_ID \"$oid").as[JsString].value

    //get common link and attach in message
    val base_url = common.Util.basePath
    val transfer_url = common.Util.basePath + "/borrow/"+ ret_transfer_obj_id

    //CREATE message
    to_user_ids.foreach({user_id =>
      Message.create(Json.obj(
        Message.KW_FROM -> JsNull,
        Message.KW_TO -> JsString(user_id) ,
        Message.KW_TITLE -> "Borrow Request",
        Message.KW_MSG -> JsString("A user requet to borrow items: <a href=\""+transfer_url+"\">"+transfer_url+"</a>")
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
  def updateStatus(id:String,status:String) = {
    this.update(id ,Json.obj(KW_STATUS -> status))
  }
}
