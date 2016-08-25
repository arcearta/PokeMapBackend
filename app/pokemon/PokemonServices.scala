package pokemon

import java.io.{BufferedReader, IOException, InputStream, InputStreamReader}

import POGOProtos.Map.Pokemon.MapPokemonOuterClass.MapPokemon
import POGOProtos.Map.Pokemon.NearbyPokemonOuterClass.NearbyPokemon
import POGOProtos.Map.Pokemon.WildPokemonOuterClass.WildPokemon
import POGOProtos.Networking.Envelopes.RequestEnvelopeOuterClass.RequestEnvelope.AuthInfo
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeTokenRequest, GoogleClientSecrets, GoogleTokenResponse}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.pokegoapi.api.PokemonGo
import com.pokegoapi.api.map.MapObjects
import com.pokegoapi.api.map.pokemon.CatchablePokemon
import com.pokegoapi.util.Log
import dto._
import okhttp3.OkHttpClient
import com.pokegoapi.auth._

import scala.collection.JavaConversions._

/**
  * Created by arcearta on 2016/07/26.
  */


class PokemonServices extends App{

  override def main(args: Array[String]) {
    println("Hello, world")
  }

  def convertStreamToString(is : InputStream) : String = {
    def inner(reader : BufferedReader, sb : StringBuilder) : String = {
      val line = reader.readLine()
      if(line != null) {
        try {
          inner(reader, sb.append(line + "\n"))
        } catch {
          case e : IOException => e.printStackTrace()
        } finally {
          try {
            is.close()
          } catch {
            case e : IOException => e.printStackTrace()
          }
        }

      }
      sb.toString()
    }

    inner(new BufferedReader(new InputStreamReader(is)), new StringBuilder())
  }

  @throws[IOException]
  private def refreshMyToken(auth_code: String): String = {
    System.out.println("auth_code on refreshMyToken: " + auth_code)

    var refreshToken: String = "";
    val saveCode = UserSession.findSession(auth_code)

    println("saveCode: " + saveCode)

    if(saveCode.isDefined){
      System.out.println("refresh token encontrado: " + saveCode)
      refreshToken = saveCode.get
    }else{
      val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance, new BufferedReader(new InputStreamReader(getUrl)))
      println("paso1: " +clientSecrets.getDetails.getClientSecret )
      println("autcode: " +auth_code )
      val tokenResponse: GoogleTokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport,
                                                                                  JacksonFactory.getDefaultInstance,
                                                                                  "https://www.googleapis.com/oauth2/v4/token",
                                                                                    clientSecrets.getDetails.getClientId,
                                                                                    clientSecrets.getDetails.getClientSecret, auth_code, "").execute
      println("paso2: " + tokenResponse )
      val accessToken: String = tokenResponse.getAccessToken
      refreshToken= tokenResponse.getRefreshToken
      System.out.println("New refresh token: " + refreshToken)
      if(refreshToken != null && !"".equals(refreshToken) )
        UserSession.saveSession(auth_code, refreshToken)
    }
    refreshToken
  }

  def getUrl: InputStream = {
    val dato = this.getClass.getResourceAsStream("/" + "client_secret.json")
    println("--------------------")
    //println(convertStreamToString(dato))
    println("--------------------")
    dato
  }

  def authenticate(token : Option[String], http : OkHttpClient): (CredentialProvider, OkHttpClient) = {
    var auth:GoogleCredentialProvider = null

    /*if (token.isDefined && !"".equals(token.get)) {

      println("Autenticando en google")
      val refres = refreshMyToken(token.get)
      println("Autenticando en google: refr : " + refres)
      if (refres == null || "".equals(refres)) {
        println("Autenticando con usuario y password")
        auth = new GoogleAutoCredentialProvider(http, "iotpruebas@gmail.com", "Sura2016")
      } else {
        auth = new GoogleCredentialProvider(http, refres)
      }
      //auth = new GoogleCredentialProvider(http, findPokemon.token.get); // currently uses oauth flow so no user or pass needed
    } else {
      println("Autenticando en PTC")
      auth = new PtcCredentialProvider(http, "pokeservices", "pokeservices")
    } dsf*/
    println("autenticando con itpruebas")
    //auth = new GoogleAutoCredentialProvider(http, "iotpruebas@gmail.com", "Sura2016")
    val provider: GoogleUserCredentialProvider = new GoogleUserCredentialProvider(http)
    provider.login(token.get)
    //provider
    auth = new GoogleCredentialProvider(http, provider.getRefreshToken)
    //auth = new GoogleCredentialProvider(http, token.get)
    (auth, http)
  }

  def getRefresh(findPokemon: FindPokemon) = {
    val refres = refreshMyToken(findPokemon.token.get)
    println("New Refres generado: " + refres)
    refres
  }

  def getAllNearPokemons(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient
    var listPokemons = List[PokemonPosition]()
    try {
      val datos = authenticate(findPokemon.token, http)

      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      val catchablePokemon: List[MapPokemon] = spawnPoints.getCatchablePokemons.toList
      println("catchablePokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, cp.getExpirationTimestampMs, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
      listPokemons

     /* val nearbyPokemons: List[NearbyPokemon] = spawnPoints.getNearbyPokemons.toList
      println("nearbyPokemons in area:" + nearbyPokemons.size)

      nearbyPokemons.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, 0, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
      listPokemons*/


    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }

  def getCatchablePokemons(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient

    var listPokemons = List[PokemonPosition]()

    try {

      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      val catchablePokemon: List[MapPokemon] = spawnPoints.getCatchablePokemons.toList

      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, cp.getExpirationTimestampMs, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
      listPokemons
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }


  def getNearPokemon(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient

    var listPokemons = List[PokemonPosition]()

    try {

      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      val catchablePokemon: List[WildPokemon] = spawnPoints.getWildPokemons.toList

      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {

        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonData.getPokemonId.getNumber, cp.getPokemonData.getPokemonId.name, cp.getTimeTillHiddenMs, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
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
      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
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

    try {
      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)
      Thread.sleep(4000)

      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)

      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      println("Point in area:" + spawnPoints.isComplete)
      println("PokeStops :" + spawnPoints.getPokestops.size())

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

      /*catchablePokemon.foreach(cp => {
        val encResult: EncounterResult = cp.encounterPokemon
        if (encResult.wasSuccessful) {
          println("Encounted:" + cp.getPokemonId)
          val result: CatchResult = cp.catchPokemonWithRazzBerry
          println("Attempt to catch:" + cp.getPokemonId + " " + result.getStatus)
        }
      })*/


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
