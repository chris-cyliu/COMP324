package model

import play.api.libs.json.{Json, JsObject}
import play.mvc.Http.Session

/**
 * Created by fafa on 17/2/15.
 */
object Session {

  val KW_USER_OBJ = "user_obj"
  def getUserObject(s:Session):JsObject = {
    Json.parse(s.get(KW_USER_OBJ)).asInstanceOf[JsObject]
  }
}
