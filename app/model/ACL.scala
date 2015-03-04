package model

import play.api.libs.json._

/**
 * Json Foramt [{
 * type : user , group
 * id : may be user id , group id depends on type
 * right: [READ, WRITE]
 * }.......]
 */
object ACL {

  /**
   * Parsing rule by using the
   * @param value
   * @return
   */
  def apply(value:JsArray):Seq[ACLRule] = {
    value.value.map({
      value =>
      val v_type = (value \ KW_TYPE).as[JsString].value match {
        case "user" => User
        case "group" => Group
      }

      val id = (value \ KW_ID).as[JsString].value
      val right = (value \ KW_RIGHT).as[JsArray].value.map({
        v:JsValue=>
          v.as[JsString].value match {
            case "r" => Read
            case "w" => Write
          }
      })

      ACLRule(v_type,id,right)
    })
  }

  /**
   * Return a list of AclRight
   * @param user
   * @param rules
   * @return
   */
  def getPermission(user:JsValue , rules:Seq[ACLRule]):Set[AclRight] = {
    //user scan
    //group scan
    val userId:String = (user \ model.User.KW_ID).as[JsString].value
    val groupIds:Seq[String] = (user \ model.User.KW_GROUP).as[JsArray].value.map(_.as[JsString].value)

    val acl_rights = rules.foldLeft(Set[AclRight]())({
      (a:Set[AclRight],b:ACLRule) =>
        b.aclType match{
          case User =>
            if(b.id == userId){
              a ++ b.right
            }else{
              a
            }
          case Group =>
            val find_gp = groupIds.filter(_ == b.id)

            if(find_gp.size > 0){
              a ++ b.right
            }else
              a
        }
    })

    acl_rights
  }


  val KW_TYPE = "type"
  val KW_ID = "id"
  val KW_RIGHT = "right"

  case class ACLRule(aclType: AclType, id:String, right: Seq[AclRight])

  trait AclType
  object Group extends AclType
  object User extends AclType

  object AclRight {

  }
  trait AclRight
  object Read extends AclRight
  object Write extends AclRight
}


