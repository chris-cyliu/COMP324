package model

import play.api.libs.json.{JsObject, JsValue}
import play.modules.reactivemongo.json.collection.JSONCollection

/**
 * {
 *    name:
 *    tag:[]
 *    items:[
 *      {
 *         serial:
 *         comment : \\
 *         owner : \\owner_id
 *      }
 *    ]
 * }
 *
 */
object Item extends AbstractObject{

  override val collection_name: String = "item"


  def addSerial(item_id:String, serial_num:String,)
  /**
   * Assign location attr in the object
   * Add Transfer log
   * @param item_id
   * @param serial_num
   * @param owner_id
   */
  def assign(item_id:String, serial_num:String , location_id:String) = {

  }
}
