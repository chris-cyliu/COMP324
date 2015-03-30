package model

import java.util.{TimeZone, Calendar}

import error.MongodbException
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.BSONFormats
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import reactivemongo.api.{QueryOpts}
import reactivemongo.bson.{BSONObjectID, BSONDateTime}
import reactivemongo.core.commands.Count

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Standard Object with common attributes
 * {
 *    _id
 *    created
 *    updated
 * }
 */
abstract class AbstractObject {

  lazy val collection = ReactiveMongoPlugin.db.collection[JSONCollection](collection_name)
  val KW_ID = "_id"
  val KW_UPDATED = "updated"
  val KW_CREATED = "created"
  val KW_ACL = "acl"
  val MAX_WAIT = Duration(50000,MILLISECONDS)
  val collection_name:String

  //CU action of object
  def create(temp:JsValue) : JsObject = {

    val in = temp.as[JsObject]
    //set time
    var save_object = in + (KW_UPDATED -> BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())))

    save_object \ KW_ID match {
      case _:JsUndefined =>
        save_object = save_object + (KW_CREATED -> BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())))+(KW_ID->BSONFormats.toJSON(BSONObjectID.generate))
    }

    val err = Await.result(collection.insert(save_object),MAX_WAIT)
    err.errMsg match{
      case Some(emsg) =>
        throw new MongodbException(emsg)
      case _ =>
    }

    save_object
  }

  //Delete action
  def delete(id:String) = {
    Await.result(collection.remove(Json.obj(KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(id).get))),MAX_WAIT)
  }

  def bulkInsert(docs:JsArray):Unit ={
    //TODO: error handle
    bulkInsert(docs.value)
  }

  def bulkInsert(docs:Seq[JsValue]):Unit ={
    //TODO: error handle
    Await.result(collection.bulkInsert(Enumerator.enumerate(docs)),MAX_WAIT)
  }

  def count()(implicit query:JsValue = Json.obj()):Int = {
    Await.result(collection.db.command(Count(this.collection_name)),MAX_WAIT)
  }

  /**
   * Simple update field for obj
   * @param id
   * @param update
   * @return
   */
  def update(id:String , update:JsValue) = {
    val id_obj = Json.obj("_id"->BSONFormats.toJSON(BSONObjectID.parse(id).get))
    var nUpdate = update.as[JsObject]

    //update "UPDATED" timestamp
    nUpdate = nUpdate + (KW_UPDATED , BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis())))
    Await.result(collection.update(id_obj, Json.obj("$set"->nUpdate)),MAX_WAIT)
  }

  def get(id:String):JsValue = {
    this.list(0,1)(Json.obj(KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(id).get)))(0)
  }

  //List action
  def list(offset:Int, item_per_page:Int)(implicit query:JsValue = Json.obj()):Seq[JsObject] = {
    Await.result(collection.find(query).options(QueryOpts(offset,item_per_page)).cursor[JsObject].collect[List](item_per_page),MAX_WAIT)
  }

  def list(offset:Int, item_per_page:Int,query:JsValue,projection:JsValue):Seq[JsObject] = {
    Await.result(collection.find(query,projection).options(QueryOpts(offset,item_per_page)).cursor[JsObject].collect[List](item_per_page),MAX_WAIT)
  }

  /**
   * For custom update with more support mongo op
   * @param id
   * @param update
   * @return
   */
  def customUpdate(id:String, update:JsValue) = {
    val id_obj = Json.obj("_id"->BSONFormats.toJSON(BSONObjectID.parse(id).get))

    //update "UPDATED" timestamp
    val nUpdate = Json.obj("$set"-> Json.obj(KW_UPDATED-> BSONFormats.toJSON(BSONDateTime(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()))))
    Await.result(collection.update(id_obj,nUpdate ++ update.as[JsObject]),MAX_WAIT)
  }

  /**
   * Function to add a acl rule
   * @param id
   * @param _type
   */
  def updateAcl(id:String,act_id:String,level:Int, _type:String) = {
    //check previous record
    val query_userid = Json.obj(Feature.KW_ACL -> Json.obj(
      "$elemMatch"->Json.obj(
        "id" -> act_id
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
            ACL.KW_ID ->act_id,
            ACL.KW_RIGHT -> level,
            ACL.KW_TYPE -> _type
          )
          )
        )
      Await.result(collection.update(query_id,op),MAX_WAIT)
    }else{
      //TODO: Exist ?
    }
  }

  /**
   * Function to get list of object by id
   * @param id
   * @return
   */
  def getListByIds(id:Seq[String]) = {
    val list_id_obj = id.map({
      x =>
        Json.obj(
          KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(x).get)
        )
    })
    val selector = Json.obj(
      "$or"->JsArray(list_id_obj)
    )

    this.list(0,Int.MaxValue)(selector)
  }
}
