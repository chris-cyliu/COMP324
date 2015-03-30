package controllers

import model.{ACL, Feature, AbstractObject}
import play.api.libs.json.{ Json}
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object FeatureController extends ResourceController{
  override val obj: AbstractObject = Feature

  def getFeatureByUserid(userid:String) = Action{
    val query = Json.obj(Feature.KW_ACL -> Json.obj(
        "$elemMatch"->Json.obj(
          ACL.KW_ID -> userid
        )
      )
    )

    val projection = Json.obj(Feature.KW_ACL -> Json.obj(
      "$elemMatch"->Json.obj(
        ACL.KW_ID -> userid
      )
    ),
    "name" ->1)
    val ret = Feature.list(0,Int.MaxValue , query, projection)
    Ok(Json.obj("data"->ret))
  }
}
