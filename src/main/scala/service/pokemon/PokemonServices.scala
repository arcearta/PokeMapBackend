package service.pokemon

import POGOProtos.Map.Pokemon.MapPokemonOuterClass.MapPokemon
import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo
import com.pokegoapi.api.PokemonGo
import com.pokegoapi.api.map.MapObjects
import com.pokegoapi.api.map.pokemon.{CatchResult, CatchablePokemon, EncounterResult}
import com.pokegoapi.util.Log
import dto._
import okhttp3.OkHttpClient
import com.pokegoapi.auth._;

import scala.collection.JavaConversions._

/**
  * Created by arcearta on 2016/07/26.
  */
class PokemonServices {

  def getNearPokemon(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient
    var auth: AuthInfo = null

    var listPokemons = List[PokemonPosition]()

    try {

      var auth:CredentialProvider = null

      if(findPokemon.token.isDefined){
        //val auth2 = new PtcCredentialProvider(http, ExampleLoginDetails.LOGIN, ExampleLoginDetails.PASSWORD);
        auth = new GoogleCredentialProvider(http, findPokemon.token.get); // currently uses oauth flow so no user or pass needed
      }else{
        auth = new PtcCredentialProvider(http, "pokeservices", "pokeservices")
      }

      val go: PokemonGo = new PokemonGo(auth, http)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)

      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)
      val catchablePokemon: List[MapPokemon] = spawnPoints.getCatchablePokemons.toList

      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, cp.getExpirationTimestampMs, Some(Position(cp.getLatitude, cp.getLongitude))))
      })

      /*val nearPokemonList: List[NearbyPokemon] = go.getMap.getNearbyPokemon().toList
      println("Pokemon in area:" + nearPokemonList.size)

      nearPokemonList.foreach(cp => {
        val distance = cp.getDistanceInMeters
        println("Encounted:" + cp.getPokemonId + "  Distance: " + distance)
        //listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.name, None, None, None, None))
      })*/

      listPokemons
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }


  def getGyms(findPokemon: FindPokemon): List[Gym] = {
    val http: OkHttpClient = new OkHttpClient

    try {
      var auth:CredentialProvider = null

      if(findPokemon.token.isDefined){
        //val auth2 = new PtcCredentialProvider(http, ExampleLoginDetails.LOGIN, ExampleLoginDetails.PASSWORD);
        auth = new GoogleCredentialProvider(http, findPokemon.token.get); // currently uses oauth flow so no user or pass needed
      }else{
        auth = new PtcCredentialProvider(http, "pokeservices", "pokeservices")
      }

      val go: PokemonGo = new PokemonGo(auth, http)

      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      //val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.lat.get, findPokemon.lon.get)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      println("Point in area:" + spawnPoints.isComplete)
      println("Gyms :" + spawnPoints.getGyms.size())

      val listGyms = spawnPoints.getGyms.toList.map(gym => Gym(gym.getOwnedByTeam.name, Position(gym.getLatitude, gym.getLongitude)))
      listGyms
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        List[Gym]()
      }
    }
  }

  def getPokeStop(findPokemon: FindPokemon): List[Stop] = {
    val http: OkHttpClient = new OkHttpClient

    // var listPokeParadas = List[Stop]()

    try {
      var auth:CredentialProvider = null

      if(findPokemon.token.isDefined){
        println("autenticando con token en google")
        //val auth2 = new PtcCredentialProvider(http, ExampleLoginDetails.LOGIN, ExampleLoginDetails.PASSWORD);
        auth = new GoogleCredentialProvider(http, findPokemon.token.get); // currently uses oauth flow so no user or pass needed
      }else{
        println("autenticando con pokemon trainer club")
        auth = new PtcCredentialProvider(http, "pokeservices", "pokeservices")
      }

      val go: PokemonGo = new PokemonGo(auth, http)
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      //val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.lat.get, findPokemon.lon.get)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      println("Point in area:" + spawnPoints.isComplete)
      println("PokeStops :" + spawnPoints.getPokestops.size())

      // for(stop <- spawnPoints.getPokestops ){
      //  listPokeParadas = listPokeParadas ++ List(Stop(stop.getDetails.getDescription, Position(stop.getLatitude, stop.getLongitude)))
      // }

      val pokeStops = spawnPoints.getPokestops.map(stop => Stop("", Position(stop.getLatitude, stop.getLongitude)))
      pokeStops.toList

    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        List[Stop]()
      }
    }
  }


  def getCatchablePokemon {
    val http: OkHttpClient = new OkHttpClient
    try {
      val token: String = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjBiZDEwY2JmMDM2OGQ2MWE0NDBiZjYxZjNiM2EyZDI0NGExODQ5NDcifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhdF9oYXNoIjoib2ljcGdidS00Q1d1SFdLSEdNRDZ4dyIsImF1ZCI6Ijg0ODIzMjUxMTI0MC03M3JpM3Q3cGx2azk2cGo0Zjg1dWo4b3RkYXQyYWxlbS5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExMTQyMTY1MjcxMjA1NzEwMDc1MCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJhenAiOiI4NDgyMzI1MTEyNDAtNzNyaTN0N3Bsdms5NnBqNGY4NXVqOG90ZGF0MmFsZW0uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJlbWFpbCI6ImFhcmlhc3RhQGdtYWlsLmNvbSIsImlhdCI6MTQ2OTU0Nzk5NiwiZXhwIjoxNDY5NTUxNTk2fQ.CEFnZW6nikCiGiF-_YtvgiZuK7GRHDlUlGCor0ZkCYKYb2ULntMj741JMWaWnG_RScpj_lycsFrAmGlxvy9qdv-0oOM5bmOIGjYPQVBSrYXncJ5lazAHlnIplUICHgv_bfE00C_yuaShCkLgBpXoaOgHdQp86WlBqLHb8CN3NBJk2CUUKZa6skTFGDOEgTgwSE1JEaanTTKr-3b6sfod-hwTbEIsMO5IoNNma4jp7E1LACl_3VBN1hOA4ZbTvOReSSVztkcIIdPTcM8styinPAg983u5nn_fApxHcvgK-m5-SUS9KWp9EsJkVQAstbP79Dg5SJrnq3ubm0r-4Z5z9g"
      val auth = new GoogleCredentialProvider(http, token); // currently uses oauth flow so no user or pass needed
      val go: PokemonGo = new PokemonGo(auth, http)
      go.setLocation(6.254010, -75.578931, 0)

      val catchablePokemon: List[CatchablePokemon] = go.getMap.getCatchablePokemon.toList
      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {
        val encResult: EncounterResult = cp.encounterPokemon
        if (encResult.wasSuccessful) {
          println("Encounted:" + cp.getPokemonId)
          val result: CatchResult = cp.catchPokemonWithRazzBerry
          println("Attempt to catch:" + cp.getPokemonId + " " + result.getStatus)
        }
      })


      val spawnPoints: MapObjects = go.getMap.getMapObjects(6.254010, -75.578931)

      println("Point in area:" + spawnPoints.isComplete)

      spawnPoints.getGyms.toList.foreach(cp => {
        println("latitud:" + cp.getLatitude)
        println("longitud:" + cp.getLongitude)
        println("nombre:" + cp.getSponsor.name)
        println("color:" + cp.getOwnedByTeam.name)
      }
      )

    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
      }
    }
  }

}
