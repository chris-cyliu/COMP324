package model

/**
 * {
 *    type: //can be people, unit, ship
 *    name: //owner name
 *   }
 */
object Owner extends AbstractObject{
  override val collection_name: String = "owner"
}
