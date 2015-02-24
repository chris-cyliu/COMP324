package model

/**
 * Page object design to record down the acl and
 *
 * {
 *    title : \\page title
 *    acl :
 *    child : //recursive page object
 */
object Page extends AbstractObject{
  override val collection_name: String = "page"
}
