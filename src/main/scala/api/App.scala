package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentType, HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives.{complete, pathPrefix}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.PathMatchers.IntNumber
import geotrellis.raster.render.ColorRamps
import geotrellis.raster.{IntArrayTile, MultibandTile, NODATA, Tile}
import geotrellis.spark.{LayerId, SpatialKey}
import geotrellis.spark.io.{AttributeStore, SpatialKeyFormat, ValueNotFoundError, ValueReader}

import scala.concurrent.Future
import scala.io.StdIn

object App
{
  def processImage(multibandTile: MultibandTile): Tile =
  {
    val band1 = multibandTile.band(0)
    val (cols, rows) = (multibandTile.cols, multibandTile.rows)
    val data: Array[Int] = new Array[Int](cols*rows)

    var index = 0
    for(i <- 0 until cols)
    {
      for(j <- 0 until rows)
      {
        val p0: Double = band1.get(j, i)

        if(p0 > 8000)  { data(index) = 1 }
        else  { data(index) = NODATA }
        index += 1
      }
    }

    IntArrayTile(data, cols,rows)
  }

  def main(args: Array[String]): Unit =
  {
    val host = "localhost"
    val port = 7075

    implicit val system = ActorSystem("api")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val catalogPath = new java.io.File("generated/catalog").toURI
    val attributeStore: AttributeStore = AttributeStore(catalogPath)
    val valueReader: ValueReader[LayerId] = ValueReader(attributeStore, catalogPath)

    val route = pathPrefix(IntNumber / IntNumber / IntNumber){
      (z, x, y) =>
      {
        complete
        {
          Future{
            val tiles: Option[MultibandTile] =
            {
              try
              {
                val reader = valueReader.reader[SpatialKey, MultibandTile](LayerId("landsat", z))
                Some(reader.read(SpatialKey(x, y)))
              }
              catch { case _: ValueNotFoundError => None }
            }

            for (tile <- tiles) yield
            {
              val processed_tile = processImage(tile)
              HttpResponse(entity = HttpEntity(ContentType(MediaTypes.`image/png`), processed_tile.renderPng(ColorRamps.BlueToRed).bytes))
            }
          }
        }
      }
    }

    val bindingFuture = Http().bindAndHandle(route, host, port)
    println("> Server online: http://" + host + ":" + port)
    println(">> Test a client app ;)")
    println(">>> Press enter to shutdown the server")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}