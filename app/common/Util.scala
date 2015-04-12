package common

import play.api.Play
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Play.current

/**
 * Created by fafa on 20/2/15.
 */
object Util {

  lazy val basePath = Play.configuration.getString("base_url").get

  val homePagePath = basePath

  val loginPath = basePath

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

    def getSortObj(obj:Seq[(String,String)]):JsValue = {
      var ret = Json.obj()
      obj.foreach({ x =>
        val dir = if(x._2 =="desc") -1 else 1
        ret = ret + (x._1 , JsNumber(dir))
      })
      ret
    }
  }
}
