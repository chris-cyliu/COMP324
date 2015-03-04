package model

import error.MongodbException
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoPlugin
import play.modules.reactivemongo.json.BSONFormats
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Play.current
import reactivemongo.api.{DefaultDB, QueryOpts}
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

  val KW_ID = "_id"
  val KW_UPDATED = "updated"
  val KW_CREATED = "created"
  val KW_ACL = "acl"
  val MAX_WAIT = Duration(50000,MILLISECONDS)

  val collection_name:String

  lazy val collection = ReactiveMongoPlugin.db.collection[JSONCollection](collection_name)

  //CU action of object
  def create(in:JsObject) : JsObject = {

    //set time
    var save_object = in + (KW_UPDATED -> BSONFormats.toJSON(BSONDateTime(System.currentTimeMillis())))

    save_object \ KW_ID match {
      case _:JsUndefined =>
        save_object = save_object + (KW_CREATED -> BSONFormats.toJSON(BSONDateTime(System.currentTimeMillis())))+(KW_ID->BSONFormats.toJSON(BSONObjectID.generate))
    }

    val err = Await.result(collection.insert(save_object),MAX_WAIT)
    err.errMsg match{
      case Some(emsg) =>
        throw new MongodbException(emsg)
      case _ =>
    }

    save_object
  }

  //List action
  def list(page:Int, item_per_page:Int)(implicit query:JsValue = Json.obj()):Seq[JsObject] = {
    Await.result(collection.find(query).options(QueryOpts((page-1)*item_per_page,item_per_page)).cursor[JsObject].collect[List](item_per_page),MAX_WAIT)
  }

  //Delete action
  def delete(id:String) = {
    Await.result(collection.remove(Json.obj(KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(id).get))),MAX_WAIT)
  }

  def bulkInsert(docs:Seq[JsValue]):Unit ={
    //TODO: error handle
    Await.result(collection.bulkInsert(Enumerator.enumerate(docs)),MAX_WAIT)
  }

  def bulkInsert(docs:JsArray):Unit ={
    //TODO: error handle
    bulkInsert(docs.value)
  }

  def count()(implicit query:JsValue = Json.obj()):Int = {
    Await.result(collection.db.command(Count(this.collection_name)),MAX_WAIT)
  }

  def update(id:String , update:JsValue) = {
    val id_obj = BSONFormats.toJSON(BSONObjectID.parse(id).get)
    var nUpdate = update.as[JsObject]

    //update "UPDATED" timestamp
    nUpdate = nUpdate + (KW_UPDATED , BSONFormats.toJSON(BSONDateTime(System.currentTimeMillis())))
    Await.result(collection.update(id_obj, update),MAX_WAIT)
  }

  def get(id:String):JsValue = {
    this.list(1,1)(Json.obj(KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(id).get)))(0)
  }
}
