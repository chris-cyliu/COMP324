package model

/**
 * Transfer log
 * {
 *    from:
 *    to:
 *    item:
 *    date:
 * }
 */
object Transfer extends AbstractObject {
  override val collection_name: String = "transfer"
}
