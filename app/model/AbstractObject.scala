package model

import controllers.UserController._
import error.MongodbException
import org.joda.time.DateTime
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.modules.reactivemongo.json.BSONFormats
import play.modules.reactivemongo.json.collection.JSONCollection
import play.api.libs.concurrent.Execution.Implicits._
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
  val MAX_WAIT = Duration(50000,MILLISECONDS)

  val collection_name:String

  //CU action of object
  def create(collection:JSONCollection , in:JsObject) : JsObject = {

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
  def list(collection:JSONCollection , page:Int, item_per_page:Int)(implicit query:JsValue = Json.obj()):Seq[JsObject] = {
    Await.result(collection.find(query).options(QueryOpts((page-1)*item_per_page,item_per_page)).cursor[JsObject].collect[List](item_per_page),MAX_WAIT)
  }

  //Delete action
  def delete(collection:JSONCollection, id:String) = {
    Await.result(collection.remove(Json.obj(KW_ID -> BSONFormats.toJSON(BSONObjectID.parse(id).get))),MAX_WAIT)
  }

  def bulkInsert(collection:JSONCollection , docs:Seq[JsValue]):Unit ={
    //TODO: error handle
    Await.result(collection.bulkInsert(Enumerator.enumerate(docs)),MAX_WAIT)
  }

  def bulkInsert(collection:JSONCollection , docs:JsArray):Unit ={
    //TODO: error handle
    bulkInsert(collection,docs.value)
  }

  def getCollection(db:DefaultDB):JSONCollection = {
    db.collection[JSONCollection](collection_name)
  }

  def count(collection: JSONCollection)(implicit query:JsValue = Json.obj()):Int = {
    Await.result(collection.db.command(Count(this.collection_name)),MAX_WAIT)
  }
}
