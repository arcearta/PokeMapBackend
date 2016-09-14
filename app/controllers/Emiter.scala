package controllers

import com.github.nkzawa.socketio.client.{IO, Socket}

/**
  * Created by arcearta on 2016/09/11.
  */
object Emiter {

  var mSocket: Socket = IO.socket("http://chat.socket.io")

  def init(): Unit ={

    mSocket.connect()
  }

  def attemptSend(): Unit ={
    mSocket.emit("new message", "texto del mensaje")
  }


}
