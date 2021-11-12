

package png

import geotrellis.raster.Tile
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import geotrellis.raster.render.{ColorMap, RGB}

object App
{
  def main(args: Array[String]): Unit =
  {
    val path = "generated/writed.tif"
    val geotiff: SinglebandGeoTiff = GeoTiffReader.readSingleband(path)
    val tile: Tile = geotiff.tile

    println(tile.asciiDraw())

    val colorMap = ColorMap(
      Map(
        2 -> RGB(255, 0, 0),
        4 -> RGB(0, 255, 0),
        7 -> RGB(0, 0, 255)
      )
    )

    tile.renderPng(colorMap).write("generated/rendered.png")
  }
}