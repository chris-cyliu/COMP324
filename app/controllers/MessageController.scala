package controllers

import model.{User, Session, Message, AbstractObject}
import play.api.libs.json.{JsString, JsArray, Json}
import play.api.mvc.Action
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONObjectID

/**
 * Created by Chris on 4/3/15.
 */
object MessageController extends ResourceController {
  override val obj: AbstractObject = Message

  def getMessage() = Action{
    implicit request =>
      val user_obj =Json.parse(request.session.get(Session.KW_USER_OBJ).get)
      val user_id = (user_obj \ User.KW_ID \ "$oid").as[JsString].value

      val selector = Json.obj(
        "$query"-> Json.obj(
          Message.KW_TO -> user_id),
        "$orderby" -> Json.obj("created"-> -1)

      )

      val data = obj.list(0,Int.MaxValue)(selector)
      Ok(Json.obj("data" -> data))
  }
}
