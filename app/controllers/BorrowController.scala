package controllers

import controllers.TransferController._
import model.{Transfer, Borrow, AbstractObject}
import play.api.libs.json.Json
import play.api.mvc.Action

/**
 * Created by fafa on 29/3/15.
 */
object BorrowController extends ResourceController{
  override val obj: AbstractObject = Borrow

  def respondRequest(borrow_id:String) = Action{
    val json_stringify = Json.stringify(obj.get(borrow_id))
    Ok(views.html.layout("Respond borrow request",views.html.BorrowRespond(json_stringify)))
  }

  def approve(id:String) = Action {
    Borrow.updateStatus(id,Borrow.KW_APPROVED)
    Ok(Json.obj(
      "success"->""
    ))
  }

  def reject(id:String) = Action {
    Borrow.updateStatus(id,Borrow.KW_DENIED)
    Ok(Json.obj(
      "success"->""
    ))
  }
}
