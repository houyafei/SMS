package tencentsms.util

import java.lang.reflect.{ParameterizedType, Type}

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

// scalastyle:off named.argument
object JsonMapper {
  private val Mapper: ObjectMapper = (new ObjectMapper() with ScalaObjectMapper).registerModule(DefaultScalaModule)
      .registerModule(new JodaModule())

  def to[T](value: => T): String = {
    Mapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, true)
    Mapper.writeValueAsString(value)
  }

  def from[T](value: String)(implicit m: Manifest[T]): T = {
    Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    Mapper.readValue[T](value, typeReference[T])
  }

  def tryFrom[T](value: String)(implicit m: Manifest[T]): Either[String, T] = {
    try {
      Mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      val result = Mapper.readValue[T](value, typeReference[T])
      Right(result)
    } catch {
      case es: Exception => Left("error")
    }
  }

  private def typeReference[T: Manifest] = new TypeReference[T] {
    override def getType = typeFromManifest(manifest[T])
  }

  private def typeFromManifest(m: Manifest[_]): Type = {
    if (m.typeArguments.isEmpty) {
      m.runtimeClass
    } else {
      new ParameterizedType {
        def getRawType = m.runtimeClass

        def getActualTypeArguments = m.typeArguments.map(typeFromManifest).toArray

        def getOwnerType = m.runtimeClass
      }
    }
  }
}
