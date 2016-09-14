package controllers

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import dto.PokemonPosition

/**
  * Created by arcearta on 2016/09/11.
  */
object Emmiter {

  var config:Configuration  = new Configuration()
  //config.setHostname("50.116.54.176")
  config.setHostname("localhost")
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
    server.getBroadcastOperations().sendEvent(postalCode, listPokemon)

    /*
    server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
      @Override
      public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
        server.getBroadcastOperations().sendEvent("chatevent", data);
      }
    });*/
  }


}
