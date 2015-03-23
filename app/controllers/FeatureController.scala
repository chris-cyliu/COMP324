package controllers

import model.{Feature, AbstractObject}
import play.api.libs.json.{JsValue, JsString, JsArray, Json}
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object FeatureController extends ResourceController{
  override val obj: AbstractObject = Feature
  
  def getFeatureByUserid(userid:String) = Action{
    val query = Json.obj(Feature.KW_ACL -> Json.obj(
        "$elemMatch"->Json.obj(
          "id" -> userid
        )
      )
    )
    var ret = Feature.list(0,Int.MaxValue)(query)
    Ok(Json.obj("data"->ret))
  }

  /**
   * [{userid: , featureid},{userid: , featureid},{userid: , featureid}]
   * @return
   */
  def updateFeature() = Action(parse.json){
    implicit request =>
      val array_pair = request.body.as[JsArray]
      array_pair.value.foreach({
        //foreach add to Feature
        a:JsValue =>
          Feature.updateAcl((a\"featureid").as[JsString].value,(a\"userid").as[JsString].value,"user")
      })
      Ok(Json.obj(
        "success" -> JsString("")
      ))
  }
}
