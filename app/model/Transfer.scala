package model

/**
 * Transfer log
 * {
 *    from: location_id
 *    to: location_id
 *    items:[{
 *      item_id:
 *      serial:
 *    }]
 *    date:
 *    status: //pending,approved,denied
 * }
 */
object Transfer extends AbstractObject {
  override val collection_name: String = "transfer"
}
