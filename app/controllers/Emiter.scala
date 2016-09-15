package controllers

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import dto.{Stop, _}
import play.api.libs.json.Json

/**
  * Created by arcearta on 2016/09/11.
  */
object Emmiter {

  implicit val tokenFormat = Json.writes[Token]
  implicit val tokenReadFormat = Json.reads[Token]
  implicit val pokemonPositionFormat = Json.writes[Position]
  implicit val pokemonPositionReadFormat = Json.reads[Position]

  implicit val pokemonInfoFormat = Json.writes[PokemonPosition]

  implicit val findPokemonFormat = Json.writes[FindPokemon]
  implicit val findPokemonReadFormat = Json.reads[FindPokemon]

  implicit val findGymFormat = Json.writes[Gym]
  implicit val findStopFormat = Json.writes[Stop]

  var config:Configuration  = new Configuration()
  config.setHostname("50.116.54.176")
  //config.setHostname("localhost")
  config.setPort(9092)

  val server:SocketIOServer  = new SocketIOServer(config)

  init()


  def init(): Unit ={
    server.start();
    //Thread.sleep(Integer.MAX_VALUE);
  }

  def stop(): Unit ={
    server.stop();
  }

  def sendMessageString(postalCode:String, mensaje:String): Unit ={
    server.getBroadcastOperations().sendEvent(postalCode, mensaje)
  }

  def sendMessage(postalCode:String, listPokemon: List[PokemonPosition]): Unit ={
      val datos = Json.toJson(listPokemon)
      println("Pokemons en emitter: " + datos)
      server.getBroadcastOperations().sendEvent(postalCode, datos )

    /*
    server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
      @Override
      public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
        server.getBroadcastOperations().sendEvent("chatevent", data);
      }
    });*/
  }


}
