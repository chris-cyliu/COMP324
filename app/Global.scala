import model.{ACL, User, Feature}
import play.api.{Logger, Application, GlobalSettings}
import play.api.libs.json.{JsObject, JsArray, Json}
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by fafa on 20/2/15.
 */
object Global extends GlobalSettings {
  override def onError(request: RequestHeader, ex: Throwable) = {
    Future.successful(InternalServerError(
      Json.obj{"error" -> ex.getLocalizedMessage()}
    ))
  }

  override def onStart(app:Application): Unit = {
    if(!Feature.exist()){
      Logger.info("Start init Feature and User")
      createInitDB
    }
  }

  def createInitDB: Unit = {
    //for the first time launch this app
    // check the existence of colleciton Page and User
    // if not exist => Load default data
    //  - Feature
    //  - Add adminstration user

      //Add admin account
      val user_json = Json.obj(
        User.KW_USERNAME -> "admin" ,
        User.KW_PASSWORD -> "admin",
        User.KW_DISPLAYNAME -> "admin"
      )
      val user_id = User.create(user_json) \ "_id" \ "$oid"

      //insert feature
      val acl_obj = JsArray(Json.obj(
        ACL.KW_ID -> user_id,
        ACL.KW_RIGHT -> 5,
        ACL.KW_TYPE -> "user"
      )::Nil)

      var seq_js_obj = Json.parse(scala.io.Source.fromInputStream(getClass.getResourceAsStream("public/StandradPageObj.txt")).map(_.toByte).toArray).as[JsArray].value
      seq_js_obj =  seq_js_obj.map({
        x =>
          val obj = x.as[JsObject]
          obj - Feature.KW_ACL + (Feature.KW_ACL -> acl_obj)
      })
      val collection = Feature.collection

      //clean up
      Await.result(collection.remove(Json.obj()),Duration(3000,MILLISECONDS))

      Feature.bulkInsert(seq_js_obj)
  }
}