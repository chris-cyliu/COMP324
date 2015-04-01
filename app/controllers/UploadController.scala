package controllers

import java.io.File
import java.nio.charset.Charset
import java.util.UUID

import play.api.libs.json.{JsString, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created by Chris on 16/3/15.
 */
object UploadController extends Controller{

  val local_location = "public/upload"
  val view_path = "/assets/upload"

  /**
   * Request: A file
   * Respond : a link
   * @return
   */
  def uploadFile() = Action(parse.multipartFormData){request =>
    request.body.file("file").map { file =>
      import java.io.File
      val extension = file.filename.substring(file.filename.lastIndexOf('.'))
      val filename = UUID.randomUUID()+extension
      val contentType = file.contentType
      file.ref.moveTo(new File(s"$local_location/$filename"))
      Ok(Json.obj(
        "path" -> JsString(view_path+"/"+filename)
      ))
    }.get
  }

  def getFile(file:String) = Action{
    implicit request =>
      val src = new File(local_location+File.separator+file)
      if(src.exists()){
        val buffer = scala.io.Source.fromFile(src)(Charset.forName("ISO-8859-1")).map(_.toByte).toArray
        Ok(buffer).as("image")
      }
      else
        NotFound
  }
}
