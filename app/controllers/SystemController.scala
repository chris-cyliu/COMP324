package controllers

import model.{Session}
import common.Util
import play.api.Play
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.api.Play.current

/**
 * Created by fafa on 20/2/15.
 */
object SystemController extends Controller with MongoController {

//  def getMenu = Action {
//    val menu_items = Page.list(1, Int.MaxValue)
//    Ok(Json.toJson(menu_items))
//  }

  def homepage = Action {
    request =>
      request.session.get(Session.KW_USER_OBJ) match {
        case None =>
          Ok(views.html.login())
        case Some(e) =>
          Ok(views.html.layout("Homepage",views.html.homepage()))
      }
  }
}
