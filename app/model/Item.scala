package model

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
}
