package controllers

import play.api._
import play.api.libs.json.{Json}
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(Json.obj("hello" -> "world"))
  }

}
