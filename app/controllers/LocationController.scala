package controllers

import model._
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{Action}

/**
 * Created by fafa on 15/3/15.
 */
object LocationController extends ResourceController{
  override val obj: AbstractObject = Location

  def getViewableLocation() = Action{
    implicit request =>
      val user_id = (Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ User.KW_ID \"$oid").as[JsString].value
      val data = Location.getViewableLocation(user_id)
      Ok(Json.obj(
        "data" -> data
      ))
  }
}
