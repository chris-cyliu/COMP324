package model

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json._
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
  val KW_SERIAL = "serial"
  val KW_SERIAL_SERIAL = "serial"
  val KW_SERIAL_CURRENT_LOCATION  = "cur_location"
  val KW_SERIAL_OWN_LOCATION = "own_location"

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
  }

  /**
   * Return a list of serial by item_id and location_id
   * @param item_id
   * @param location_id
   */
  def getSerial(item_id:String , location_id:String ): Seq[JsValue] ={
    val selector = Json.obj(
      KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(item_id).get),
      KW_SERIAL -> Json.obj(
        "$elemMatch" -> Json.obj{
          KW_SERIAL_OWN_LOCATION -> location_id
        }
      )
    )

    val projection = Json.obj(
      KW_SERIAL+"."+KW_SERIAL_SERIAL -> 1
    )
    this.list(0,Int.MaxValue,selector,projection)
  }

  def aduit(location_id:String):Seq[JsValue] = {
    val selector = Json.obj(
      KW_SERIAL -> Json.obj(
        "$elemMatch" -> Json.obj(
          "$or" -> JsArray(
            Json.obj(KW_SERIAL_CURRENT_LOCATION ->location_id)::
            Json.obj(KW_SERIAL_OWN_LOCATION -> location_id):: Nil)
        )
      )
    )

    val projection= Json.obj(
      KW_SERIAL -> Json.obj(
        "$elemMatch" -> Json.obj(
          "$or" -> JsArray(
            Json.obj(KW_SERIAL_CURRENT_LOCATION ->location_id)::
              Json.obj(KW_SERIAL_OWN_LOCATION -> location_id):: Nil)
        )
      ),
      "name" -> 1
    )

    val item_list = this.list(0,Int.MaxValue)(selector)
    //cal the total of each item in the item_List
    item_list.map({
      item=>
        val new_item = item.as[JsObject]
        val count_own_cur = (new_item \ KW_SERIAL).as[JsArray].value.map({
          x:JsValue =>
            val is_own = if((x \ KW_SERIAL_OWN_LOCATION).as[JsString].value == location_id) 1 else 0
            val is_cur = if((x \ KW_SERIAL_CURRENT_LOCATION).as[JsString].value == location_id) 1 else 0
            (is_own,is_cur)
        }).reduce({
          (a,b)=>
            (a._1+b._1,a._2+b._2)
        })
        new_item + ("count_own" -> JsNumber(count_own_cur._1)) + ("count_cur" -> JsNumber(count_own_cur._2))
    })

  }

}
