package controllers

import model.{Item, AbstractObject}
import play.api.mvc.Action

/**
 * Created by Chris on 24/2/15.
 */
object ItemController extends ResourceController {
  override val obj: AbstractObject = Item

  def pageBorrowItem = Action {
    Ok(views.html.layout("Borrow Item",views.html.borrowItem()))
  }
}
