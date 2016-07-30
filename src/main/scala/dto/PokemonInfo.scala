package dto

/**
  * Created by arcearta on 2016/07/17.
  */
case  class PokemonInfo(name : String , typePokemon: String)

case  class FindPokemon(token : Option[String], width : Int, name : Option[String], position: Option[Position])

case  class PokemonPosition(id : Int, name : String, timeToHide : Long, position: Option[Position])

case  class Position(latitud : Double, longitud : Double)

case  class Gym(teamColor: String, position: Position)

case  class Stop(description : String, position: Position)




