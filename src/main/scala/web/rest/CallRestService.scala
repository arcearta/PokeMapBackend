package web.rest

import java.util.concurrent.TimeUnit

import dispatch.Defaults._
import dispatch.{url, _}
import org.json4s.DefaultFormats

import spray.json._


import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Try}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Try}

/**
  * CallRestService.
  * Created by arcearta on 02/12/2014.
  */
object CallRestService  {

  private val DEFAULT_DURATION = 4

  val timeout: Duration = Duration.create(DEFAULT_DURATION, TimeUnit.SECONDS)

  /*
  def validarToken(urlCall: String, params: String): Future[Option[Autorized]] = {
    val request = url(urlCall).POST
      .setBody(params)
      .addHeader("Content-Type", "application/json")

    Http(request.> { response =>
      if (response.getStatusCode == 200) {
        Some(authorizeObjectFromJson(response.getResponseBody))
      } else {
        logger.debug(
          "respuesta validacion token no fue ok. resp = {}, cod = {}",
          response.getResponseBody, response.getStatusCode.toString
        )
        None
      }
    }).recover {
      case ex =>
        //logger.error(s"CallRestService.validarToken: ${ex.getMessage}", ex)
        None
    }
  }

  def lastAccess(serviceUrl: String, params: String): Future[String] =
    try {
      val request =
        url(serviceUrl)
          .POST
          .setBody(params)
          .addHeader("Content-Type", "application/json")

      Http(request.>(f => {
        if (f.getStatusCode == 200) {
          val entryDate = f.getResponseBody.parseJson.convertTo[LastEntryDate]
          entryDate.date
        } else {
          throw new SecurityException("Error processing lastAccess. " + f.getResponseBody)
        }
      }))
    } catch {
      case e: Throwable =>
       // logger.error(s"CallRestService.lastAcces: ${e.getMessage}", e)
        //Todo que se iba a hacer con eso?
        throw new SecurityException("Error processing lastAccess. " + e.getCause)
    }

*/

  //constant manager //http://lnfdllo.suranet.com/constantmanager/rest/constantes/r2d3/CREDENTIAL_SERVICE_PASSWORD/valor
  //https://pokevision.com/map/data/34.0089404989527/-118.49765539169312
  /*def credentialServicePassword(urlCall: String): String =
    try {
      val request = url(urlCall).GET
        .addHeader("Content-Type", "application/json")
      val response = Http(request OK as.String)

      Await.result(response, timeout)
    } catch {
      case e: Throwable =>
        //logger.error(s"CallRestService.credentialServicePassword: ${e.getMessage}", e)
        throw e
    }*/

  def getActivePokemons(urlCall: String, long : String, lat : String ): String =
    try {
      val urlFinal = urlCall + long + "/" + lat;
      println("Urll llamada: " + urlFinal)
      val request = url(urlFinal).GET
        .addHeader("Content-Type", "application/json")
      val response = Http(request OK as.String)

      Await.result(response, timeout)
    } catch {
      case e: Throwable =>
        //logger.error(s"CallRestService.credentialServicePassword: ${e.getMessage}", e)
        throw e
    }

}
