package controllers

import model.{Page,Session}
import common.Util
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController

/**
 * Created by fafa on 20/2/15.
 */
object SystemController extends Controller with MongoController{

  def getMenu = Action {
    val menu_items = Page.list(1,Int.MaxValue)
    Ok(Json.toJson(menu_items))
  }

  def homepage = Action {
    request =>
      request.session.get(Session.KW_USER_OBJ) match {
        case None =>
          Redirect(Util.loginPath)
        case Some(e) =>
          Ok(views.html.layout("Homepage",views.html.homepage()))
      }

  }
}
