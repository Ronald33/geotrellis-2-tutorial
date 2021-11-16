package polygon

import geotrellis.proj4.LatLng
import geotrellis.raster.{DoubleConstantNoDataCellType, MultibandTile}
import geotrellis.spark.{LayerId, SpatialKey, TileLayerMetadata}
import geotrellis.spark.io.{AttributeStore, CollectionLayerReader, Intersects, SpatialKeyFormat, tileLayerMetadataFormat}
import geotrellis.vector.Polygon

object App
{
  def main(args: Array[String]): Unit =
  {
    val zoom = 15

    val id: LayerId = LayerId("landsat", zoom)
    val catalogPath = new java.io.File("generated/catalog").toURI
    val attributeStore: AttributeStore = AttributeStore(catalogPath)
    val layerMetadata = attributeStore.readMetadata[TileLayerMetadata[SpatialKey]](id)
    val collectionReader: CollectionLayerReader[LayerId] = CollectionLayerReader(attributeStore, catalogPath)

    val polygon = Polygon((-71.04764,-15.756736), (-71.04764,-15.75211), (-71.036997,-15.75211), (-71.036997,-15.756736), (-71.04764,-15.756736))
    val polygon_reprojected = polygon.reproject(LatLng, layerMetadata.crs)

    val result = collectionReader.query[SpatialKey, MultibandTile, TileLayerMetadata[SpatialKey]](id).where(Intersects(polygon_reprojected)).result

    var count = 0

    result.withContext { rdd =>
      rdd.mapValues { tile =>
        tile.convert(DoubleConstantNoDataCellType).combineDouble(1, 4) { (g, ir) =>
          if((g - ir) / (g + ir) > 0.4)
          {
            count = count + 1
            1
          }
          else { Double.NaN }
        }
      }
    }

    println("Count: " + count)
  }
}