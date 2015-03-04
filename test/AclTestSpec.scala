import model.ACL.ACLRule
import model.{ACL, User}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsString, JsArray, Json, JsValue}

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import play.api.test._
import play.api.test.Helpers._
/**
 *  by fafa on 3/3/15.
 */
@RunWith(classOf[JUnitRunner])
class AclTestSpec extends Specification {

  val user: JsValue = Json.obj(
    User.KW_GROUP -> JsArray(JsString("g1") :: Nil),
    User.KW_ID -> "s1"
  )

  "ACL" should {
    "Simple Right [Read]] for GROUP" in {
      val acl_rules = ACLRule(ACL.Group, "g1", ACL.Read :: Nil) :: Nil
      ACL.getPermission(user, acl_rules) must contain(ACL.Read)
      ACL.getPermission(user, acl_rules) must have size (1)
    }

    "Simple Right [Write , Read]] for GROUP" in {
      val acl_rules = ACLRule(ACL.Group, "g1", ACL.Write :: ACL.Read::Nil) :: Nil
      ACL.getPermission(user, acl_rules) must containAllOf(ACL.Write::ACL.Read::Nil)
      ACL.getPermission(user, acl_rules) must have size (2)
    }

    "Simple Right [Write , Read]] for GROUP" in {
      val acl_rules = ACLRule(ACL.Group, "g2", ACL.Write :: ACL.Read::Nil) :: Nil
      ACL.getPermission(user, acl_rules) must have size (0)
    }

    "Composite Right [Write , Read]] for User, GROUP" in {
      val acl_rules = ACLRule(ACL.Group, "g1", ACL.Read::Nil)::ACLRule(ACL.User, "s1", ACL.Write ::Nil) :: Nil
      ACL.getPermission(user, acl_rules) must have size (2)
      ACL.getPermission(user, acl_rules) must containAllOf(ACL.Write::ACL.Read::Nil)
    }
  }
}
