package tencentsms.util

import java.io.OutputStream
import java.net.{HttpURLConnection, URL}

import org.slf4j.LoggerFactory

import scala.io.Source

object HttpPost {
  val log = LoggerFactory.getLogger("HttpPost")

  def sendPost(url: String, jsonData: String) = {
    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("POST")
    connection.setDoInput(true)
    connection.setDoOutput(true)
    connection.setRequestProperty("Accept", "application/json") // 设置接收数据的格式

    connection.setConnectTimeout(2000)
    connection.setReadTimeout(10000)
    import java.io.{BufferedWriter, OutputStreamWriter}
    import java.nio.charset.StandardCharsets
    val outputStream: OutputStream = connection.getOutputStream
    val bfw: BufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
    bfw.write(jsonData)
    bfw.flush()
    log.info("SendMsg:"+jsonData+"; Response:"+connection.getResponseCode + "," + connection.getResponseMessage)
     Source.fromInputStream(connection.getInputStream, "UTF-8").getLines().foldRight("")((res,s)=>{res.concat(s)})
  }

}
