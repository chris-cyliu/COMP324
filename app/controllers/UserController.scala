package controllers

import common.{Util, MissRequestParam}
import model.{Session, User}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

import scala.concurrent.Future

object UserController extends ResourceController{

  val collection = User.getCollection(db)

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
      User.create(collection,user_json)
      Ok("{\"success\":\"\"}")
  }

  def pageCreateUser =Action{
    Ok(views.html.layout("Create User",views.html.createUser()))
  }

  def listAction:Action = {
    implicit request:RequestHeader
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
          Ok(Json.obj("data" -> JsArray(User.list(collection,page,itemNum)),
                      "total_num" -> User.count(collection)))
        case Accepts.Html() =>
          Ok(views.html.layout("List User",views.html.listUser()))
      }
  }

  override def list = Action{
    implicit  request =>
      render {
        case Accepts.Json()=>

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
      User.login(collection,username,password) match {
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


  def addGroup = ???
}
