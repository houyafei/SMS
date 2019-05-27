package tencentsms

import tencentsms.util.{HttpPost, JsonMapper, TencentOnLineKey, TencentSignUtil, TencentTestKey}
import tencentsms.SendMsgUtil.log


object TentSmsStatistic {

  def obtainSentMsgCount(env: Int,beginDate:String,endDate:String): MsgCountResponse = {

    var appKey = ""
    var sdkappid = ""

    val time = TencentSignUtil.buildTime
    val randomNum =TencentSignUtil.random()


    env match {
      case Env.TestEnv =>
        sdkappid = TencentTestKey.sdkappid
        appKey = TencentTestKey.appKey
      case Env.OnlineEnv =>
        sdkappid = TencentOnLineKey.sdkappid
        appKey = TencentOnLineKey.appKey
      case Env.NoThing =>
    }


        val begin = beginDate.toInt
        val end = endDate.toInt

      val res = MsgCountRequest(begin, end, TencentSignUtil.buildSig(s"appkey=$appKey&random=$randomNum&time=$time"), time)
      val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/pullsendstatus?sdkappid=$sdkappid&random=$randomNum", JsonMapper.to(res))
      log.info(resultStr)
      val result = JsonMapper.from[MsgCountResponse](resultStr)


    result
  }

  def obtainPackageInfos(env: Int): List[PackageInfo] = {
    val length = 10
    var offset = 0
    var appKey = ""
    var sdkappid = ""
    var flag = true
    var tmpList = List[PackageInfo]()

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

    while (flag && !appKey.equals("")) {
      val res = PackageRequest(offset, length, TencentSignUtil.buildSig(s"appkey=$appKey&random=$random&time=$time"), time)
      val resultStr = HttpPost.sendPost(s"https://yun.tim.qq.com/v5/tlssmssvr/getsmspackages?sdkappid=$sdkappid&random=$random", JsonMapper.to(res))
      val result = JsonMapper.from[PackageResponse](resultStr)
      tmpList = tmpList ::: result.data
      offset = length + offset
      if (result.total < length) {
        flag = false
      }
    }
    tmpList

  }

}


case class PackageRequest(offset: Int, length: Int, sig: String, time: Long)

case class PackageInfo(from_time: String, to_time: String, create_time: String, amount: Int, `type`: Int, package_id: Int, used: Int)

case class PackageResponse(total: Int, data: List[PackageInfo])


case class MsgCountRequest(begin_date: Int, end_date: Int, sig: String, time: Long)

case class MsgCountData(bill_number: Int, request: Int, success: Int)

case class MsgCountResponse(result: Int, errmsg: String, data: MsgCountData)
