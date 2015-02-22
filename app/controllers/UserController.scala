package controllers

import common.{Util, MissRequestParam}
import model.{Session, User}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object UserController extends Controller with MongoController {

  val user_collection = User.getCollection(db)

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
      User.create(user_collection,user_json)
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
  def listUser =Action{
    implicit request =>
      render {
        case Accepts.Json() =>
          val page = request.getQueryString("page").getOrElse("1").toInt
          val itemNum = request.getQueryString("itemNum").getOrElse("20").toInt
          Ok(Json.obj("data" -> JsArray(User.list(user_collection,page,itemNum)),
                      "total_num" -> User.count(user_collection)))
        case Accepts.Html() =>
          Ok(views.html.layout("List User",views.html.listUser()))
      }
  }

  def listAllUser = Action{
    implicit request =>
      render {
        case Accepts.Json() =>
          Ok(Json.obj("data" -> JsArray(User.list(user_collection,1,Int.MaxValue)),
            "total_num" -> User.count(user_collection)))
        case Accepts.Html() =>
          Ok(views.html.layout("List User",views.html.listUser()))
      }
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
      User.login(user_collection,username,password) match {
        case Some(e) =>
          //construct redirect
          Ok(Util.getRedirectJsObj(Util.homePagePath)).withSession(Session.KW_USER_OBJ -> e.toString)
        case None =>
          throw new Exception("Wrong username and password")
      }
  }

  def remove(id:String) = Action{
    request =>
      User.delete(user_collection , id)
      Ok("{\"success\":\"\"}")
  }

  def logout = Action{
    request =>
      Redirect(Util.loginPath).withNewSession

  }


  def addGroup = ???
}
