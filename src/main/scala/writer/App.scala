package writer

import geotrellis.proj4.{CRS, LatLng}
import geotrellis.raster.IntArrayTile
import geotrellis.raster.io.geotiff.GeoTiff
import geotrellis.vector.Extent

import java.util.Arrays // Importacion de un paquete java

object App
{
  def main(args: Array[String]): Unit =
  {
    val array1: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7, 8, 9) // Creamos un array

    println(Arrays.toString(array1)) // Es posible usar paquetes de java

    val tile1: IntArrayTile = IntArrayTile(array1, 3, 3) // Transformamos nuestro array en un TILE, usamos como parametros el array, el numero de columnas y el numero de filas

    println(tile1.asciiDraw()) // Nos permite visualizar en tile en forma de matriz

    // Para crear un raster sera necesario establecer la extension que este ocupara en el mapa, esto se realiza con un cuadrante asignando 2 puntos
    val extent1: Extent = Extent(0, 0, 10, 10)

    // Luego de tener la extension que ocupara el mapa (mediante 2 coordenadas) sera necesario establecer a que sistema de coordenadas obedecera dicha extension
    // Entonces sera necesario crear una variable que indique el CRS, en este caso sera el: EPSG:4326
    val crs: CRS = LatLng

    // Para crear un raster sera necesario asiganar un tile, la extension que ocupara y un sistema de referencia de coordenadas
    val raster1 = GeoTiff(tile1, extent1, crs)

    raster1.write("generated/writed.tif")
  }
}