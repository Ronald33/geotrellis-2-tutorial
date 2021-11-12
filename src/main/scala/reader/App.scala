package reader

import geotrellis.raster.{IntArrayTile, NODATA, Tile}
import geotrellis.raster.io.geotiff.{GeoTiff, SinglebandGeoTiff}
import geotrellis.raster.io.geotiff.reader.GeoTiffReader

object App
{
  def main(args: Array[String]): Unit =
  {
    val path: String = "generated/writed.tif" // Ruta de la imagen satelital

    // Almacenamos el geotiff en una variable
    val geotiff: SinglebandGeoTiff = GeoTiffReader.readSingleband(path)

    // Podemos obtener diversas datos del geotiff, tales como el tile, el crs, la extension, etc.
    val cols: Int = geotiff.cols
    val rows: Int = geotiff.rows
    val tile: Tile = geotiff.tile

    // Crearemos un array en el cual almacenaremos los valores de nuestro tile modificado
    val data: Array[Int] = new Array[Int](cols*rows)

    // Crearemos un indice para recorrer el array
    var index = 0

    // Crearemos un nuevo raster en el que aquellos "pixeles" que no alcancen el umbral no seran considerados
    // Para ello crearemos el umbral
    val threshold = 5

    for(i <- 0 until cols)
    {
      for(j <- 0 until rows)
      {
        var pixel = tile.get(j, i)
        if(pixel < threshold)  { data(index) = NODATA } // Si no supera el umbral establecido no sera considerado
        else  { data(index) = pixel } // En el caso de que si cumpla el umbral se mantendra el valor del pixel, es decir sera el mismo que el de la imagen original

        index += 1 // Movemos el indice, para que sea el siguiente valor del array en el que se escriba la nueva informacion
      }
    }

    val modified_tile = IntArrayTile(data, tile.cols, tile.rows) // Creamos el tile con la data modificada
    println(modified_tile.asciiDraw()) // Como se observa aquellos valores que no superaron el umbral tienen el valor ND

    val modified_raster = GeoTiff(modified_tile, geotiff.extent, geotiff.crs) // Creamos el raster
    modified_raster.write("generated/modified.tif")
  }
}