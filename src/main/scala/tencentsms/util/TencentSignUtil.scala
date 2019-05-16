package tencentsms.util

import java.security.MessageDigest

import scala.util.Random

object TencentSignUtil {

  def buildSig(paramString: String): String = {
    val sha1Inst: MessageDigest = MessageDigest.getInstance("SHA-256")
    sha1Inst.update(paramString.getBytes())
    byte2Hex(sha1Inst.digest())
  }


  private def byte2Hex(bits: Array[Byte]): String = {
    val buffer: StringBuffer = new StringBuffer()
    bits.foreach(b => {
      val s: String = Integer.toHexString(b & 0xFF)
      if (s.length==1) buffer.append("0")
      buffer.append(s)
    })

    buffer.toString
  }


  def random(): String = {
    new Random(Long.MaxValue).nextLong().toString
  }

  def buildTime = {
    System.currentTimeMillis() / 1000
  }
}
