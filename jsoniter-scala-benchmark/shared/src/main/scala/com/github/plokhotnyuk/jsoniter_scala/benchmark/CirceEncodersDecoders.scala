package com.github.plokhotnyuk.jsoniter_scala.benchmark

import java.time.Instant
import java.util.Base64
import com.github.plokhotnyuk.jsoniter_scala.benchmark.BitMask.toBitMask
import io.circe.Decoder._
import io.circe.Encoder._
import io.circe._
import io.circe.generic.extras._
import io.circe.generic.extras.codec.UnwrappedCodec
import io.circe.generic.extras.semiauto._
import scala.collection.immutable.{BitSet, IntMap}
import scala.collection.mutable
import scala.util.Try

object CirceEncodersDecoders {
  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true, reuseWriters = true, predictSize = true)
  val prettyPrinter: Printer = Printer.spaces2.copy(dropNullValues = true, reuseWriters = true, predictSize = true)
  val escapingPrinter: Printer = printer.copy(escapeNonAscii = true)
  implicit val config: Configuration = Configuration.default.withDefaults.withDiscriminator("type")
  implicit val adtC3c: Codec[ADTBase] = deriveConfiguredCodec[ADTBase]
  implicit val anyValsC3c: Codec[AnyVals] = {
    implicit def anyValCodec[A <: AnyVal : UnwrappedCodec]: Codec[A] = implicitly

    deriveConfiguredCodec[AnyVals]
  }
  val base64C3c: Codec[Array[Byte]] =
    Codec.from(Decoder.decodeString.map[Array[Byte]](Base64.getDecoder.decode),
      Encoder.encodeString.contramap[Array[Byte]](Base64.getEncoder.encodeToString))
  implicit val bidRequestC3c: Codec[OpenRTB.BidRequest] = {
    import io.circe.generic.extras.auto._

    deriveConfiguredCodec[OpenRTB.BidRequest]
  }
  implicit val bigIntE5r: Encoder[BigInt] = encodeJsonNumber
    .contramap(x => JsonNumber.fromDecimalStringUnsafe(new java.math.BigDecimal(x.bigInteger).toPlainString))
  implicit val bitSetC3c: Codec[BitSet] =
    Codec.from(Decoder.decodeArray[Int].map(arr => BitSet.fromBitMaskNoCopy(toBitMask(arr, Int.MaxValue /* WARNING: It is an unsafe option for open systems */))),
      Encoder.encodeSet[Int].contramapArray((m: BitSet) => m))
  implicit val mutableBitSetC3c: Codec[mutable.BitSet] =
    Codec.from(Decoder.decodeArray[Int].map(arr => mutable.BitSet.fromBitMaskNoCopy(toBitMask(arr, Int.MaxValue /* WARNING: It is an unsafe option for open systems */))),
      Encoder.encodeSeq[Int].contramapArray((m: mutable.BitSet) => m.toVector))
  implicit val distanceMatrixC3c: Codec[GoogleMapsAPI.DistanceMatrix] = {
    import io.circe.generic.auto._

    deriveConfiguredCodec[GoogleMapsAPI.DistanceMatrix]
  }
  implicit val gitHubActionsAPIC3c: Codec[GitHubActionsAPI.Response] = {
    implicit val c1: Codec[GitHubActionsAPI.Artifact] =
      Codec.forProduct9("id", "node_id", "name", "size_in_bytes", "url", "archive_download_url",
        "expired", "created_at", "expires_at") {
        (id: Long, node_id: String, name: String, size_in_bytes: Long, url: String, archive_download_url: String,
        expired: String, created_at: Instant, expires_at: Instant) =>
          GitHubActionsAPI.Artifact(id, node_id, name, size_in_bytes, url, archive_download_url,
            expired.toBoolean, created_at, expires_at)
      } { a =>
        (a.id, a.node_id, a.name, a.size_in_bytes, a.url, a.archive_download_url,
        a.expired.toString, a.created_at, a.expires_at)
      }
    deriveConfiguredCodec[GitHubActionsAPI.Response]
  }
  implicit val extractFieldsC3c: Codec[ExtractFields] = deriveConfiguredCodec[ExtractFields]
  implicit val geoJSONC3c: Codec[GeoJSON.GeoJSON] = {
    implicit val c1: Codec[GeoJSON.SimpleGeometry] = deriveConfiguredCodec[GeoJSON.SimpleGeometry]
    implicit val c2: Codec[GeoJSON.Geometry] = deriveConfiguredCodec[GeoJSON.Geometry]
    implicit val c3: Codec[GeoJSON.SimpleGeoJSON] = deriveConfiguredCodec[GeoJSON.SimpleGeoJSON]
    deriveConfiguredCodec[GeoJSON.GeoJSON]
  }
  implicit val intMapC3c: Codec[IntMap[Boolean]] =
    Codec.from(Decoder.decodeMap[Int, Boolean].map(_.foldLeft(IntMap.empty[Boolean])((m, p) => m.updated(p._1, p._2))),
      Encoder.encodeMap[Int, Boolean].contramapObject((m: IntMap[Boolean]) => m))
  implicit val longMapC3c: Codec[mutable.LongMap[Boolean]] =
    Codec.from(Decoder.decodeMap[Long, Boolean].map(_.foldLeft(new mutable.LongMap[Boolean]) { (m, p) =>
      m.update(p._1, p._2)
      m
    }), Encoder.encodeMapLike[Long, Boolean, mutable.Map].contramapObject((m: mutable.LongMap[Boolean]) => m))
  implicit val missingRequiredFieldsC3c: Codec[MissingRequiredFields] = deriveConfiguredCodec[MissingRequiredFields]
  implicit val nestedStructsC3c: Codec[NestedStructs] = deriveConfiguredCodec[NestedStructs]
  implicit val suitC3c: Codec[Suit] =
    Codec.from(decodeString.emap(s => Try(Suit.valueOf(s)).fold[Either[String, Suit]](_ => Left("Suit"), Right.apply)),
      encodeString.contramap[Suit](_.name))
  implicit val suitADTC3c: Codec[SuitADT] = deriveEnumerationCodec[SuitADT]
  implicit val suitEnumC3c: Codec[SuitEnum.Value] = Codec.from(decodeEnumeration(SuitEnum), encodeEnumeration(SuitEnum))
  implicit val primitivesC3c: Codec[Primitives] = deriveConfiguredCodec[Primitives]
  implicit val tweetC3c: Codec[TwitterAPI.Tweet] = {
    import io.circe.generic.auto._

    deriveConfiguredCodec[TwitterAPI.Tweet]
  }
}