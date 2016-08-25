package controllers

import java.util.Calendar

import dto._
import play.api._
import play.api.mvc._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.cache.Cache
import play.api.Play.current
import play.api.data.Form
import play.api.libs.json.Format
import play.api.libs.json.JsValue
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.db._
import pokemon.PokemonServices
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global._

trait Protocols {
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

  val pokemonList = List(
    PokemonPosition(1, "Pikachu", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(2, "Magikarp", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(3, "Rattata", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(4, "Hitmonchan", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(5, "Snorlax", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(6, "Jynx", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(7, "Grimer", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(8, "Koffing", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(9, "Drowzee", 0, Some(Position(-123.0881,38.3845))),
    PokemonPosition(10, "Drowzee", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(11, "Ditto", 0, Some(Position(-122.0881,37.3845))),
    PokemonPosition(12, "Squirtle", 0, Some(Position(-122.0881,37.3845)))
  )

  val gymList = List(
    Gym("RED", Position(6.25376517, -75.56812763)),
    Gym("BLUE", Position(6.254115,-75.578935))
  )

  val stopList = List(
    Stop("", Position(6.25395714, -75.56051016)),
    Stop("", Position(6.254085,-75.579945))
  )



  val activePokList = List(
    PokemonPosition(1, "Pikachu", Calendar.getInstance().getTime().getTime + 1000 , Some(Position(6.25024572 , -75.56576729))),
    PokemonPosition(5, "Magikarp", Calendar.getInstance().getTime().getTime + 2000, Some(Position(6.24815536, -75.5616389))),
    PokemonPosition(9, "Magikarp", Calendar.getInstance().getTime().getTime + 2000, Some(Position(6.24927536, -75.5607389))),
    PokemonPosition(3, "Magikarp", Calendar.getInstance().getTime().getTime + 2000, Some(Position(6.24618536, -75.5628389)))
  )



  def index = Action {
    Ok(views.html.index(null))
  }

  def findPokemon = Action {
    Ok( Json.toJson(pokemonList))
  }

  def getRefresh = Action.async(parse.json) { implicit request =>

    request.body.validate[FindPokemon].map{
      case find => {
        val pokemonService = new PokemonServices
        val ref = pokemonService.getRefresh(find)
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
