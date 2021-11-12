package catalog

import geotrellis.raster.MultibandTile
import geotrellis.raster.render.ColorRamps
import geotrellis.spark.{LayerId, SpatialKey}
import geotrellis.spark.io.{AttributeStore, SpatialKeyFormat, ValueReader}

object App
{
  def main(args: Array[String]): Unit =
  {
    val catalogPath = new java.io.File("generated/catalog").toURI
    val attributeStore: AttributeStore = AttributeStore(catalogPath)
    val valueReader: ValueReader[LayerId] = ValueReader(attributeStore, catalogPath)

    val zoom = 13
    val x = 2479
    val y = 4459

    val reader = valueReader.reader[SpatialKey, MultibandTile](LayerId("landsat", zoom))
    val tile = reader.read(SpatialKey(x, y))
    tile.band(0).renderPng(ColorRamps.BlueToRed).write("generated/from_catalog.png")
  }
}