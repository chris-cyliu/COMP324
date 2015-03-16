package controllers

import model.{Group, AbstractObject}
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object GroupController extends ResourceController{
  override val obj: AbstractObject = Group

  def page = Action {
    Ok(views.html.layout("User Group Management",views.html.userGroupManagement()))
  }
}
