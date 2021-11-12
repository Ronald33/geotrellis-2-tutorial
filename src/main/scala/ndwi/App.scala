package ndwi

import geotrellis.raster.{ArrayTile, BitArrayTile, DoubleConstantNoDataCellType, IntArrayTile, MultibandTile, NODATA, Tile, isData}
import geotrellis.raster.io.geotiff.{GeoTiff, MultibandGeoTiff}
import geotrellis.raster.io.geotiff.reader.GeoTiffReader

import java.util

object App
{
  def main(args: Array[String]): Unit =
  {
    val path = "files/multiband.tif"

    val geotiff: MultibandGeoTiff = GeoTiffReader.readMultiband(path)

    val tile: MultibandTile = geotiff.tile
    val cols: Int = tile.cols
    val rows: Int = tile.rows

    println("cols: " + cols)
    println("rows: " + rows)

    /*
    val data: Array[Int] = new Array[Int](cols*rows)
    var index = 0
    val green_band = tile.band(1)
    val nir_band = tile.band(4)
    for(i <- 0 until cols)
    {
      for(j <- 0 until rows)
      {
        val green: Double = green_band.get(j, i)
        val nir: Double = nir_band.get(j, i)
        val ndwi: Double = (green - nir) / (green + nir)
        if(ndwi > 0.4)  { data(index) = 1 }
        else  { data(index) = NODATA }
        index += 1
      }
    }
    val generated_tile = IntArrayTile(data, cols, rows)
    */

    // https://github.com/geotrellis/geotrellis-landsat-tutorial/blob/master/src/main/scala/tutorial/CreateNDWIPng.scala

    // /*
    val generated_tile: Tile = tile.convert(DoubleConstantNoDataCellType).combineDouble(1, 4) { (g, ir) =>
      if(isData(g) && isData(ir))
      {
        if((g - ir) / (g + ir) > 0.4) { 1 }
        else { Double.NaN }
      }
      else { Double.NaN }
    }
    // */

    val generated_raster = GeoTiff(generated_tile, geotiff.extent, geotiff.crs) // Creamos el raster
    generated_raster.write("generated/ndwi.tif")
  }
}