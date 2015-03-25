package controllers

import common.{Util, MissRequestParam}
import model.{AbstractObject, Session, User}
import play.api.libs.json.{JsArray, JsObject, JsString, Json}
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.BSONFormats
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONObjectID

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
        Ok(views.html.layout("User Management",views.html.userManagement()))
    }

    /**
     * Request for a list of user information by userid
     * @return
     */
    def getUsers() = Action(parse.json){request =>
      val users_id = request.body.as[JsArray]
      val array_obj_id = users_id.value.map({
        id=>
          Json.obj("_id"->BSONFormats.toJSON(BSONObjectID.parse(id.as[JsString].value).get))
      })
      val jsArray_users_id = JsArray(array_obj_id)
      val query = Json.obj("$or"->jsArray_users_id)
      val ret = User.list(0,Int.MaxValue)(query)
      Ok(Json.obj("data"->ret))
    }

    override val obj: AbstractObject = User
  }
