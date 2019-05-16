package tencentsms

import org.slf4j.LoggerFactory
import tencentsms.util.{HttpPost, JsonMapper, TencentOnLineKey, TencentSignUtil, TencentTestKey}

object SendMsgUtil {
  val log = LoggerFactory.getLogger("SendMsgUtil")

  def sendMsg(phone: String, code: String): SmsResponse = {
    val phoneStr = phone.split("-")
    var tel: Telphone = Telphone(phone)
    if (phoneStr.length == 2) {
      tel = Telphone(phone.split("-")(1), phone.split("-")(0))
    }
    val random = TencentSignUtil.random()
    val res = TencentMsgReq(tel, Array(code), random)
    val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=${TencentOnLineKey.sdkappid}&random=$random", JsonMapper.to(res))
    log.info(phone + ":" + resultStr)
    JsonMapper.from[SmsResponse](resultStr)
  }

  def sendMessage(tel: Telphone, content: Array[String], tmpId: Int, evn: Int): SmsResponse = {
    log.info(JsonMapper.to(tel) + ": begin send msg" )
    var sdkappid = ""
    var appKey = ""
    evn match {
      case Env.OnlineEnv => {
        sdkappid = TencentOnLineKey.sdkappid
        appKey = TencentOnLineKey.appKey
      }
      case Env.TestEnv => {
        sdkappid = TencentTestKey.sdkappid
        appKey = TencentTestKey.appKey
      }
    }
    val random = TencentSignUtil.random()
    val res = TencentMsgReq(tel, content, random, appKey, tmpId)
    val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=$sdkappid&random=$random", JsonMapper.to(res))
    val response = JsonMapper.from[SmsResponse](resultStr)
    log.info(JsonMapper.to(tel) + ":" + JsonMapper.to(response))
    response
  }
}

case class Telphone(mobile: String, nationcode: String = "86")

case class TencentMsgRes(result: Int, errmsg: String, ext: String, fee: Int, sid: String)

class TencentMsgReq(val tel: Telphone) {

  val ext: String = ""
  val extend: String = ""
  var params: Array[String] = Array()
  var sig: String = ""
  var time: Long = 0L
  var tpl_id: Int = TencentOnLineKey.tmpId

  private def buildTime(timeStamp: Long) = {
    time = timeStamp
  }

  private def buildSig(sign: String): Unit = {
    sig = sign
  }

  private def buildParams(params: Array[String]): Unit = {
    this.params = params
  }


}

object TencentMsgReq {
  def apply(tel: Telphone, params: Array[String], random: String, appKey: String = TencentTestKey.appKey, tpl_id: Int = TencentOnLineKey.tmpId): TencentMsgReq = {
    val tencentMsgReq = new TencentMsgReq(tel)
    tencentMsgReq.buildTime(TencentSignUtil.buildTime)
    tencentMsgReq.buildSig(TencentSignUtil.buildSig(s"appkey=$appKey&random=$random&time=${tencentMsgReq.time}&mobile=${tel.mobile}"))
    tencentMsgReq.buildParams(params)
    tencentMsgReq.tpl_id = tpl_id
    tencentMsgReq
  }
}


case class TencentStatusReq(max: Int = 100, sig: String, time: Long, `type`: Int = 0)

case class SingleUserStatus()

case class SmsResponse(result: Int, errmsg: String)

object Env {
  val NoThing = 0
  val TestEnv = 1
  val OnlineEnv = 2
}
