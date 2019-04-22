package tencentsms

object SendMsgUtil {

  def sendMsg(phone: String, code: String): SmsResponse = {
    val tel = Telphone(phone)
    val random = TencentSignUtil.random()
    val res = TencentMsgReq(tel, Array(code), random)
    val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/sendsms?sdkappid=${TencentKey.sdkappid}&random=$random", JsonMapper.to(res))
    JsonMapper.from[SmsResponse](resultStr)
  }
}

case class Telphone(mobile: String, nationcode: String = "86")

case class TencentMsgRes(result: Int, errmsg: String, ext: String, fee: Int, sid: String)

class TencentMsgReq(val tel: Telphone) {

  val ext: String = ""
  val extend: String = ""
  var params: Array[String] = Array("1234")
  var sig: String = ""
  //  var sign: String = "迅游手游"
  var time: Long = 0L
  val tpl_id: Int = TencentKey.tmpId

  private def buildTime(timeStamp: Long) = {
    time = timeStamp
  }

  private def buildSig(sign: String): Unit = {
    sig = sign
    println(sig)
  }

  private def buildParams(params: Array[String]): Unit = {
    this.params = params
  }


}

object TencentMsgReq {
  def apply(tel: Telphone, params: Array[String], random: String): TencentMsgReq = {
    val tencentMsgReq = new TencentMsgReq(tel)
    tencentMsgReq.buildTime(TencentSignUtil.buildTime)
    tencentMsgReq.buildSig(TencentSignUtil.buildSig(s"appkey=${TencentKey.appKey}&random=$random&time=${tencentMsgReq.time}&mobile=${tel.mobile}"))
    tencentMsgReq.buildParams(params)
    tencentMsgReq
  }
}


case class TencentStatusReq(max: Int = 100, sig: String, time: Long, `type`: Int = 0)

case class SingleUserStatus()

case class SmsResponse(result: Int, errmsg: String)
