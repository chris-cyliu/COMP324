package model

/**
 * Represent message object of the system
 * {
 *    from:
 *    to:
 *    msg:
 *    status : "READ", "UNREAD" , "DELETE"
 * }
 *
 */
object Message extends AbstractObject{
  override val collection_name: String = "message"


  val KW_FROM = "from"
  val KW_TO = "to"
  val KW_MSG = "msg"
  val KW_STATUS = "status"
  val KW_STATUS_READ = "READ"
  val KW_STATUS_UNREAD = "UNREAD"
  val KW_STATUS_DELETE = "DELETE"
}
