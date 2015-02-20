package controllers

import common.MissRequestParam
import model.{Session, User}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection

object UserController extends Controller with MongoController {

  val user_collection = db.collection[JSONCollection](User.collection_name)
  /**
   * Method: POST
   *
   * Form Request:
   *  data: json object of User
   *
   * Response :
   *
   * @return
   */
  def createUser = Action(parse.urlFormEncoded){
    request =>
      val user_json = Json.parse(request.body.get("data").getOrElse(throw MissRequestParam("data"))(0)).as[JsObject]

      User.create(user_collection,user_json)

      Ok("Success")
  }

  /**
   * Request:
   *  page  :
   *  ItemNum :
   *
   * Response:
   *  Json
   *    "data" -> Array of a list of user object
   * @return
   */
  def listUser =Action{
    request =>
      val page = request.getQueryString("page").getOrElse("1").toInt
      val itemNum = request.getQueryString("itemNum").getOrElse("20").toInt
      Ok(Json.obj("data" -> JsArray(User.list(user_collection,page,itemNum))))
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
          Ok(e).withSession(Session.KW_USER_OBJ -> e.toString)
        case None =>
          throw new Exception("Wrong username and password")
      }
  }

  def addGroup = Action
}
