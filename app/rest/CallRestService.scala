package rest

import java.util.concurrent.TimeUnit

import dispatch.Defaults._
import dispatch._
import dto.{Stop, _}
import org.json4s.DefaultFormats
import play.api.libs.json.Json
import spray.json._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Try}


/**
 * CallRestService.
 * Created by arcearta on 02/12/2014.
 */
object CallRestService {

  private val DEFAULT_DURATION = 4

  val timeout: Duration = Duration.create(DEFAULT_DURATION, TimeUnit.SECONDS)

  def sendMessage(serviceUrl: String, params: String): Future[String] =
    try {
      println("Send message to " + serviceUrl)
      println("params: " + params)
      val request =
        url(serviceUrl)
          .POST
          .setBody(params)
          .addHeader("Content-Type", "application/json")

      Http(request.>(f => {
        if (f.getStatusCode == 200) {
          println("response: " + f.getStatusCode)
          //val entryDate = f.getResponseBody.parseJson.convertTo[LastEntryDate]
          //entryDate.date
          ""
        } else {
          throw new Exception(f.getResponseBody)
        }
      }))
    } catch {
      case e: Throwable =>
        println( s"Error buscando menu: ${e.getMessage}", e)
        //Todo que se iba a hacer con eso?
        throw new SecurityException("Error processing lastAccess. " + e.getCause)
    }



  //constant manager //http://lnfdllo.suranet.com/constantmanager/rest/constantes/r2d3/CREDENTIAL_SERVICE_PASSWORD/valor
  def credentialServicePassword(urlCall: String): String =
    try {
      val request = url(urlCall).GET
        .addHeader("Content-Type", "application/json")
      val response = Http(request OK as.String)

      Await.result(response, timeout)
    } catch {
      case e: Throwable =>
        println( s"Error buscando menu: ${e.getMessage}", e)
        throw e
    }



  def credential(urlCall: String, user: String, password: String) = {
    implicit val formats = DefaultFormats
    try {
      val request = url(urlCall).GET.as_!(user, password)
        .addHeader("Content-Type", "application/json")

     // val response: Future[org.json4s.JValue] = Http(request OK as.json4s.Json)
      //val json = Await.result(response, timeout)

     // Some(json.extract[SimpleCredential])
      ""

    } catch {
      case e: Throwable =>
        println( s"Error buscando menu: ${e.getMessage}", e)
       // Option.empty[SimpleCredential]
       ""
    }
  }

  def getMenuApp(serviceUrl: String, params: String): Future[String] = {
    implicit val formats = DefaultFormats
    Try {
      val request =
        url(serviceUrl)
          .POST
          .setBody(params)
          .addHeader("Content-Type", "application/json")

      Http(
        request.>(
          f =>
            if (f.getStatusCode == 200) {
              f.getResponseBody.parseJson.toString()
            } else {
              throw new Exception(f.getResponseBody)
            }
        )
      )
    }.recoverWith {
      case e =>
        println( s"Error buscando menu: ${e.getMessage}", e)
        Failure(e)
    }.get
  }

  case class UserInfo(isAuthenticate: Boolean, username: String, fullName: String, dni: String, repository: Option[String] = None, msg: Option[String] = None)

}
