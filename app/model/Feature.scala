package model
/**
 * Page object design to record down the acl and
 *
 * {
 *    title : \\page title
 *    acl :
 *    child : //recursive page object
 */
object Feature extends AbstractObject{
  override val collection_name: String = "feature"

}
