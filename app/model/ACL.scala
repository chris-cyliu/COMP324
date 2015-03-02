package model

/**
 * type : user , group
 * id : may be user id , group id depends on type
 * right: rw, r ,n(non)
 */
object ACL {

}

trait AclRight
case class Write extends AclRight