package model

import play.api.libs.Crypto
import play.api.libs.json._
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await

/**
 * User object standard structure
 * {
 *    _id:"mongodb object id"
 *
 *    password:
 *    usename:
 *    name:
 * }
 */
object User extends AbstractObject{
  val KW_USERNAME = "username"
  val KW_PASSWORD = "pw"
  val KW_GROUP = "group"

  val collection_name = "user"

  def getUserName(in:JsObject):String = {
    in \ User.KW_USERNAME match{
      case a:JsString =>
        a.value
      case _ =>
        throw new Exception("Missing key \"$User.KW_USERNAME\"")
    }
  }

  def getPassword(in:JsObject):String = {
    Crypto.decryptAES(
      in \ User.KW_PASSWORD match {
        case a:JsString =>
          a.value
        case _ =>
          throw new Exception("Missing key \"$User.KW_PASSWORD\"")
      })
  }

  def setPassword(in:JsObject, text:String):JsObject = {
    in - User.KW_PASSWORD + (User.KW_PASSWORD -> JsString(Crypto.encryptAES(text)))
  }

  /**
   * Function to create new user
   * @param in
   * @return
   */
  override def create( in:JsObject) = {
    //check user name whether it already exist or not
    val username = in \ KW_USERNAME
    if(Await.result(collection.find(Json.obj(KW_USERNAME -> username)).cursor[JsObject].collect[List](1),MAX_WAIT).size > 0)
      throw new Exception(s"Duplicated user name $username")
    
    //encrypt password
    val in_encrypt_pw = User.setPassword(in,(in \ User.KW_PASSWORD).as[JsString].value)

    //create Location object
    val location = Json.obj(

    )

    super.create(in_encrypt_pw)
  }

  /**
   * Paginate list user
   * @param page
   * @param item_per_page
   * @return
   */
  override def list( page:Int, item_per_page:Int)(implicit query : JsValue):Seq[JsObject] = {
    //filter password field
    super.list(page,item_per_page).map(_ - KW_PASSWORD)
  }

  def login(username:String, password_plaintext:String): Option[JsObject] ={
    val crypto_pw = Crypto.encryptAES(password_plaintext)
    val ret = Await.result(collection.find(Json.obj("$and"->JsArray(Json.obj(KW_USERNAME->username)::Json.obj(KW_PASSWORD->crypto_pw)::Nil))).cursor[JsObject].collect[List](),MAX_WAIT)
    if(ret.size == 0)
      None
    else
      Some(ret(0))
  }
}