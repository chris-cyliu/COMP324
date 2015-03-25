package controllers

import model.{Location, AbstractObject, Feature}
import play.api.libs.json.Json
import play.api.mvc.Action

/**
 * Created by fafa on 15/3/15.
 */
object LocationController extends ResourceController{
  override val obj: AbstractObject = Location
}
