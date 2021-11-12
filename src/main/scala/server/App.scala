package server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.Directives.{complete, path, pathPrefix, pathSingleSlash}
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.stream.ActorMaterializer
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.render.{ColorRamps, Png}

import scala.io.StdIn

object App
{
  def pngAsHttpResponse(png: Png): HttpResponse =
  {
    HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`image/png`), png.bytes))
  }

  def main(args: Array[String]): Unit =
  {
    val host = "localhost"
    val port = 7075

    implicit val system = ActorSystem("api")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route1 = path("ping")
    {
      Directives.get
      {
        complete("pong")
        /*complete
        {
          "Hello from server"
        }*/
      }
    }

    val route2 = pathPrefix(IntNumber / IntNumber)
    {
      (x, y) =>
      {
        val suma = x + y
        complete("La suma es: " + suma)
      }
    }

    val route3 = Directives.get
    {
      Directives.concat(
        pathSingleSlash
        {
          complete("root")
        },
        route1,
        route2,
        path("image")
        {
          complete
          {
            val geotiff: SinglebandGeoTiff = GeoTiffReader.readSingleband("writed.tif")
            pngAsHttpResponse(geotiff.tile.renderPng(ColorRamps.BlueToRed))
          }
        }
      )
    }

    /*
    val bindingFuture = Http().bindAndHandle(route1, host, port)
    val bindingFuture = Http().bindAndHandle(route2, host, port)
    */
    val bindingFuture = Http().bindAndHandle(route3, host, port)
    println("Server online: http://" + host + ":" + port)
    println(">>> Press enter to shutdown the server")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}