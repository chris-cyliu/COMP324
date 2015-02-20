import controllers.UserController._
import model.User
import org.specs2.mutable.Specification
import play.api.{Application, GlobalSettings}
import play.api.libs.json.Json
import play.api.test.WithApplication
import play.modules.reactivemongo.json.collection.JSONCollection

import play.api.test._
import play.api.test.Helpers._

/**
 * Created by fafa on 18/2/15.
 */
class UserTestSpec extends Specification {

  val fakeApplicationWithGlobal = FakeApplication(withGlobal = Some(new GlobalSettings() {
    override def onStart(app: Application) { println("Hello world!") }
  }))

  "User account create" should {
    "successfully inserted" in new WithApplication {
      val user_obj = Json.obj(User.KW_USERNAME -> "foo" , User.KW_PASSWORD -> "123456")
      val user_collection = db.collection[JSONCollection](User.collection_name)

      User.create(user_collection , user_obj)
    }
  }

}
