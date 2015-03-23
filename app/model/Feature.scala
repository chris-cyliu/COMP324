package model

import play.api.libs.json.{JsArray, Json}
import play.modules.reactivemongo.json.BSONFormats
import reactivemongo.bson.BSONObjectID
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
/**
 * Page object design to record down the acl and
 *
 * {
 *    title : \\page title
 *    acl :
 *    child : //recursive page object
 */
object Feature extends AbstractObject{
  override val collection_name: String = "feature"

  /**
   * Function to add a acl rule
   * @param id
   * @param t
   */
  def updateAcl(id:String,userid:String , t:String) = {
    //check previous record
    val query_userid = Json.obj(Feature.KW_ACL -> Json.obj(
      "$elemMatch"->Json.obj(
        "id" -> userid
        )
      )
    )
    val query_id = Json.obj("_id"->BSONFormats.toJSON(BSONObjectID.parse(id).get))
    val query = Json.obj("$and" -> JsArray(query_userid::query_id::Nil))
    var ret = Feature.list(0,Int.MaxValue)(query)

    if(ret.size==0){
      //new acl to feature
      val op =
        Json.obj(
          "$push"->Json.obj(KW_ACL -> Json.obj(
            ACL.KW_ID ->userid,
            ACL.KW_RIGHT -> "w",
            ACL.KW_TYPE -> t
          )
        )
        )

      Await.result(collection.update(query_id,op),MAX_WAIT)

    }else{
      //
    }
  }
}
