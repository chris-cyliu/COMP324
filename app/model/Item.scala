package model

import common.Util
import play.api.libs.json.{JsString, Json, JsObject, JsValue}
import play.modules.reactivemongo.json.collection.JSONCollection

/**
 * {
 *    name:
 *    tag:[]
 *    items:{
 *      serial: {
 *         serial:
 *         comment : \\
 *         location : \\owner_id
 *      }
 *    }
 * }
 *
 */
object Item extends AbstractObject{

  val KW_SERIAL_LOCATION = "location"

  val KW_SERIAL_SERIAL = "serial"

  val KW_SERIAL = "serial"

  override val collection_name: String = "item"

  /**
   * Set default location to "System"
   * {
   *    location:
   *    serial:
   *  }
   *
   * @param item_id
   * @param data
   */
  def addSerial(item_id:String, data:JsValue) ={
    val location_id = (data \ KW_SERIAL_LOCATION).as[JsString].value
    val serial = (data\KW_SERIAL_SERIAL).as[JsString].value

    //create update operator
    val updateOp = Util.Mongo.setField(KW_SERIAL+"."+serial , data)
    this.update(item_id , updateOp)

    //add transfer history
    Transfer.transfer(None,location_id ,item_id , serial, Transfer.KW_APPROVED)
  }

  /**
   * Assign location attr in the object
   * Add Transfer log -> waiting for pending
   * @param item_id
   * @param serial
   */
  def assign(item_id:String, serial:String , location_id_from:String , location_id_to:String) = {
    Transfer.transfer(Some(location_id_from), location_id_to ,item_id , serial, Transfer.KW_PENDING)
  }
}
