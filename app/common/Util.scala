package common

import play.api.libs.json.{Json, JsObject}
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

  val system_group_object =
}

object MongoUtil {
  def connect
}
