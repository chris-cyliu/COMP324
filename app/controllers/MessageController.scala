package controllers

import model.{Message, AbstractObject}

/**
 * Created by Chris on 4/3/15.
 */
class MessageController extends ResourceController {
  override val obj: AbstractObject = Message


}
