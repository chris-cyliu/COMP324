import play.api.GlobalSettings
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.Future

/**
 * Created by fafa on 20/2/15.
 */
object Global extends GlobalSettings {
  override def onError(request: RequestHeader, ex: Throwable) = {
    Future.successful(InternalServerError(
      Json.obj{"error" -> ex.getLocalizedMessage()}
    ))
  }
}