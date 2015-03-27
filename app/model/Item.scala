package model

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.{JsString, Json, JsObject, JsValue}
import play.modules.reactivemongo.json.BSONFormats

import reactivemongo.bson.BSONObjectID

import scala.concurrent.Await

/**
 * {
 *    name:
 *    tag:[]
 *    items:{
 *      serial: {
 *         serial:
 *         own_location:
 *         cur_location:
 *      }
 *    }
 * }
 *
 */
object Item extends AbstractObject{

  override val collection_name: String = "item"

  /**
   * Set default location to "System"
   * {
   *    location_id:

   *    serial:
   *  }
   *
   * @param data
   */
  def addSerial(data:JsValue) ={
    val item_id = (data\"item_id").as[JsString].value
    val location_id = (data \ "location_id").as[JsString].value
    val serial = (data\ "serial").as[JsString].value

    //create update operator
    val updateOp = Json.obj("$push" -> Json.obj(
      "serial" -> Json.obj(
        "serial"->serial,
        "own_location" ->location_id,
        "cur_location" ->location_id
      )
    ))

    val selector = Json.obj("_id"->BSONFormats.toJSON(BSONObjectID.parse(item_id).get))

    Await.result(collection.update(selector, updateOp),MAX_WAIT)

    //add transfer history
//    Transfer.transfer(None,location_id ,item_id , serial, Transfer.KW_APPROVED)
  }

}
