package controllers

import common.{Util, MissRequestParam}
import model.{AbstractObject, Session, User}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future

object UserController extends ResourceController{

  /**
   * Method: POST
   *
   * Data : json
   *
   * Form Request:
   *  {
   *    username:
   *    pw:
   *  }
   *
   * Response :Success
   *
   * @return
   */
  def createUser = Action(parse.json){
    request =>
      val user_json = request.body.as[JsObject]
      User.create(user_json)
      Ok("{\"success\":\"\"}")
  }

  def pageCreateUser =Action{
    Ok(views.html.layout("Create User",views.html.createUser()))
  }

  /**
   * Request:
   *  page  :
   *  ItemNum :
   *
   * Response:
   *  Json:{
   *    "data" -> Array of a list of user object
   *    "total" -> Number of element in db
   *  }
   * @return
   */
  def listPage() = Action {
    Ok(views.html.layout("List User",views.html.listUser()))
  }
  /**
   * Method : POST
   * login user account and set session
   * @return
   */
  def login = Action(parse.json){
    request =>
      val username  = (request.body \ ("username")).as[JsString].value
      val password = (request.body \ ("password")).as[JsString].value
      User.login(username,password) match {
        case Some(e) =>
          //construct redirect
          Ok(Util.getRedirectJsObj(Util.homePagePath)).withSession(Session.KW_USER_OBJ -> e.toString)
        case None =>
          throw new Exception("Wrong username and password")
      }
  }

  def logout = Action{
    request =>
      Redirect(Util.loginPath).withNewSession

  }

  def page = Action {
    request =>
      Ok(views.html.userManagement())
  }

  override val obj: AbstractObject = User
}
