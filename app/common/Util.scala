package common

import play.api.libs.json.{Json, JsObject}

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
}
