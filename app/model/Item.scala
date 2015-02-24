package model

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

  def assign(collection:JSONCollection , item_id:String, serial_num:String , owner_id:String) = {

  }
}
