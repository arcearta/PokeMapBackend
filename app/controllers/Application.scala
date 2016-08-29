package controllers

import dto._
import play.api.mvc._
import play.api.Play.current

import play.api.db._
import pokemon.PokemonServices
import play.api.libs.json._

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.ExecutionContext.Implicits.global._

trait Protocols {

  implicit val tokenFormat = Json.writes[Token]
  implicit val tokenReadFormat = Json.reads[Token]
  implicit val pokemonPositionFormat = Json.writes[Position]
  implicit val pokemonPositionReadFormat = Json.reads[Position]

  implicit val pokemonInfoFormat = Json.writes[PokemonPosition]

  implicit val findPokemonFormat = Json.writes[FindPokemon]
  implicit val findPokemonReadFormat = Json.reads[FindPokemon]

  implicit val findGymFormat = Json.writes[Gym]
  implicit val findStopFormat = Json.writes[Stop]

}


trait Service extends Protocols {

}

object Application  extends Controller with Service {


  def index = Action {
    //Ok(views.html.index(null))
    Ok("exto")
  }


  def getHeaders = Action.async(parse.json) { implicit request =>

    println("headers: " + request.headers)

    println("body: " + request.body.toString())


    Future(Ok(Json.toJson( "" )))

  }


  def getRefresh = Action.async(parse.json) { implicit request =>

    request.body.validate[Token].map{
      case find => {
        val pokemonService = new PokemonServices
        val ref = pokemonService.getRefresh(find.auth_code)
        Future(Ok(Json.toJson( ref )))
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }
  }

  def findActivePokemon = Action.async(parse.json) { implicit request =>

    request.body.validate[FindPokemon].map{
      case find => {
        val pokemonService = new PokemonServices
        val respuesta = pokemonService.getCatchablePokemons(find)
        Future(Ok(Json.toJson(respuesta )))
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }
  }

  def findPokeStop = Action.async(parse.json) { implicit request =>

    request.body.validate[FindPokemon].map{
      case find => {
        val pokemonService = new PokemonServices
        val respuesta = pokemonService.getPokeStop(find)
        Future(Ok(Json.toJson(respuesta )))
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }
  }

  def findPokeGym = Action.async(parse.json) { implicit request =>

    request.body.validate[FindPokemon].map{
      case find => {
        val pokemonService = new PokemonServices
        val respuesta = pokemonService.getGyms(find)

        Future(Ok(Json.toJson(respuesta )))
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toFlatJson(e)))
    }
  }

  def db = Action {
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)")
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())")

      val rs = stmt.executeQuery("SELECT tick FROM ticks")

      while (rs.next) {
        out += "Read from DB: " + rs.getTimestamp("tick") + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }
}