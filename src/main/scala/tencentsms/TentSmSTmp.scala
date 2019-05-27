package tencentsms

import tencentsms.SendMsgUtil.log
import tencentsms.util.{HttpPost, JsonMapper, TencentOnLineKey, TencentSignUtil, TencentTestKey}

object TentSmSTmp {

  def obtainTmp(env: Int): List[TmpContent] = {
    val max = 50
    var offset = 0
    var appKey = ""
    var sdkappid = ""
    var flag = true
    var tmpList = List[TmpContent]()

    val time = TencentSignUtil.buildTime
    val random = TencentSignUtil.random()
    env match {
      case Env.TestEnv =>
        sdkappid = TencentTestKey.sdkappid
        appKey = TencentTestKey.appKey
      case Env.OnlineEnv =>
        sdkappid = TencentOnLineKey.sdkappid
        appKey = TencentOnLineKey.appKey
      case Env.NoThing =>
    }

    while (flag) {
      val res = TmpRequest(TencentSignUtil.buildSig(s"appkey=$appKey&random=$random&time=$time"), time, TmpPage(max, offset))
      val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/get_template?sdkappid=$sdkappid&random=$random", JsonMapper.to(res))
      log.info(resultStr)
      val result = JsonMapper.from[TmpResponse](resultStr)
      tmpList = tmpList ::: result.data
      offset = max + offset
      if (result.count < max) {
        flag = false
      }
    }
    tmpList

  }
}

case class TmpRequest(sig: String, time: Long, tpl_page: TmpPage)

case class TmpPage(max: Int, var offset: Int)

case class TmpResponse(result: Int, errmsg: String, total: Int, count: Int, data: List[TmpContent])

case class TmpContent(id: Int, international: Int, reply: String, status: Int, text: String, `type`: Int, title: String, apply_time: String, reply_time: String)
