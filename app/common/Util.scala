package common

import play.api.libs.json.{JsValue, Json, JsObject}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by fafa on 20/2/15.
 */
object Util {

  val basePath = "http://127.0.0.1:8080"

  val homePagePath = basePath

  val loginPath = basePath + "/assets/html/login.html"

  def getRedirectJsObj(path:String):JsObject={
    Json.obj("redirect"->path)
  }

  object Mongo{
    def pushArray(field_name:String, push_data:JsValue):JsValue = {
      Json.obj({
        "$push" -> Json.obj(
          field_name -> push_data
        )
      })
    }

    def setField(field_name:String, new_value:String):JsValue = {
      Json.obj({
        "$set" -> Json.obj(
          field_name -> new_value
        )
      })
    }

    def setField(field_name:String, new_value:JsValue):JsValue = {
      Json.obj({
        "$set" -> Json.obj(
          field_name -> new_value
        )
      })
    }
  }
}