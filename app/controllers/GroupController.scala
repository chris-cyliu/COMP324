package controllers

import model.{Session, User, Group, AbstractObject}
import play.api.libs.json.{JsValue, JsString, JsArray, Json}
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object GroupController extends ResourceController{
  override val obj: AbstractObject = Group

  def page = Action {
    implicit request =>
      Ok(views.html.layout("User Group Management",views.html.userGroupManagement(),User.getMenuItem((Json.parse(request.session.get(Session.KW_USER_OBJ).get) \ "_id" \ "$oid").as[JsString].value)))
  }

  def getGroupByUserid(userid:String) = Action{
    var query = Json.obj(
      Group.KW_MEMBER -> Json.obj(
        "$in"->JsArray(JsString(userid)::Nil)
      )
    )
    var ret = Group.list(0,Int.MaxValue)(query)
    Ok(Json.obj("data"->ret))
  }

  /**
   * [{userid: , groupid},{userid: , groupid},{userid: , groupid}]
   * @return
   */
  def updateGroup() = Action(parse.json){
    implicit request =>
      val array_pair = request.body.as[JsArray]
      array_pair.value.foreach({
        //foreach add to group
        a:JsValue =>
          Group.addUserToGroup((a\"userid").as[JsString].value,(a\"groupid").as[JsString].value)
      })
      Ok(Json.obj(
        "success" -> ""
      ))
  }
  
}
