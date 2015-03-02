package model

/**
 * {
 *    type: //can be people, unit, ship
 *    name: //owner name
 *   }
 */
object Location extends AbstractObject{
  override val collection_name: String = "location"

  val KW_NAME = "name"

  //Can be user , unit, devision
  val kW_TYPE = "type"

}
