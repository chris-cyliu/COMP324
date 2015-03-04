import controllers.UserController._
import model.User
import org.specs2.mutable.Specification
import play.api.libs.json.Json
import play.api.{Application, GlobalSettings}
import play.api.test.{WithApplication, FakeApplication}
import play.modules.reactivemongo.json.collection.JSONCollection


/**
 * Little hack to use
 */
class CreatePageObject extends Specification {

  val fakeApplicationWithGlobal = FakeApplication(withGlobal = Some(new GlobalSettings() {
    override def onStart(app: Application) { println("Hello world!") }
  }))

  "User account create" should {
    "successfully inserted" in new WithApplication {
      val user_obj = Json.obj(User.KW_USERNAME -> "foo" , User.KW_PASSWORD -> "123456")
      val user_collection = User.collection

      User.create(user_obj)
    }
  }
}