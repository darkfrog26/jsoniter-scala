package com.github.plokhotnyuk.jsoniter_scala.benchmark

import org.openjdk.jmh.annotations.Benchmark

class ArrayOfShortsReading extends ArrayOfShortsBenchmark {
  @Benchmark
  def avSystemGenCodec(): Array[Short] = {
    import com.avsystem.commons.serialization.json._
    import java.nio.charset.StandardCharsets.UTF_8

    JsonStringInput.read[Array[Short]](new String(jsonBytes, UTF_8))
  }

  @Benchmark
  def borer(): Array[Short] = {
    import io.bullet.borer.Json

    Json.decode(jsonBytes).to[Array[Short]].value
  }

  @Benchmark
  def circe(): Array[Short] = {
    import io.circe.parser._
    import java.nio.charset.StandardCharsets.UTF_8

    decode[Array[Short]](new String(jsonBytes, UTF_8)).fold(throw _, identity)
  }

  @Benchmark
  def circeJawn(): Array[Short] = {
    import io.circe.jawn._

    decodeByteArray[Array[Short]](jsonBytes).fold(throw _, identity)
  }

  @Benchmark
  def circeJsoniter(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.CirceJsoniterCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._
    import io.circe.Decoder

    Decoder[Array[Short]].decodeJson(readFromArray(jsonBytes)).fold(throw _, identity)
  }

  @Benchmark
  def dslJsonScala(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.DslPlatformJson._

    dslJsonDecode[Array[Short]](jsonBytes)
  }

  @Benchmark
  def jacksonScala(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.JacksonSerDesers._

    jacksonMapper.readValue[Array[Short]](jsonBytes)
  }

  @Benchmark
  def jsoniterScala(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.JsoniterScalaCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray[Array[Short]](jsonBytes)
  }

  @Benchmark
  def playJson(): Array[Short] = {
    import play.api.libs.json.Json

    Json.parse(jsonBytes).as[Array[Short]]
  }

  @Benchmark
  def playJsonJsoniter(): Array[Short] = {
    import com.evolutiongaming.jsonitertool.PlayJsonJsoniter._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray[play.api.libs.json.JsValue](jsonBytes).as[Array[Short]]
  }

  @Benchmark
  def smithy4sJson(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.Smithy4sJCodecs._
    import com.github.plokhotnyuk.jsoniter_scala.core._

    readFromArray[Array[Short]](jsonBytes)
  }

  @Benchmark
  def sprayJson(): Array[Short] = {
    import com.github.plokhotnyuk.jsoniter_scala.benchmark.SprayFormats._
    import spray.json._

    JsonParser(jsonBytes).convertTo[Array[Short]]
  }

  @Benchmark
  def uPickle(): Array[Short] = {
    import upickle.default._

    read[Array[Short]](jsonBytes)
  }

  @Benchmark
  def weePickle(): Array[Short] = {
    import com.rallyhealth.weejson.v1.jackson.FromJson
    import com.rallyhealth.weepickle.v1.WeePickle.ToScala

    FromJson(jsonBytes).transform(ToScala[Array[Short]])
  }

  @Benchmark
  def zioJson(): Array[Short] = {
    import zio.json._
    import java.nio.charset.StandardCharsets.UTF_8

    new String(jsonBytes, UTF_8).fromJson[Array[Short]].fold(sys.error, identity)
  }
}