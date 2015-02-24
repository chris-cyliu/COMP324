import controllers.UserController._
import model.{Page, User}
import org.specs2.mutable.Specification
import play.api.{Application, GlobalSettings}
import play.api.libs.json.{JsArray, Json}
import play.api.test.WithApplication
import play.modules.reactivemongo.json.collection.JSONCollection

import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by fafa on 18/2/15.
 */
class UserTestSpec extends Specification {

  val fakeApplicationWithGlobal = FakeApplication(withGlobal = Some(new GlobalSettings() {
  }))

  "Script" should {
    "create standard page object" in new WithApplication {
      val json = Json.parse(scala.io.Source.fromFile("resources/StandradPageObj.txt").map(_.toByte).toArray).as[JsArray]

      val collection = Page.getCollection(db)

      //clean up
      Await.result(collection.remove(Json.obj()),Duration(3000,MILLISECONDS))

      Page.bulkInsert(collection, json)
    }
  }
}
