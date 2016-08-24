package pokemon

import scala.collection.immutable.HashMap

/**
  * Created by arcearta on 2016/08/11.
  */
object UserSession {
  val sessiones =  new HashMap[String, String]

  def findSession(aut_code :String): Option[String] = {
    if(sessiones.contains(aut_code) ){
      sessiones.get(aut_code)
    }else
      None
  }

  def saveSession(aut_code :String, refreshToken : String) {
      sessiones -> (aut_code,refreshToken)
  }

}
