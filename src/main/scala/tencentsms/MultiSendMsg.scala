package tencentsms

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{ExecutorService, Executors}

import scala.io.Source

object MultiSendMsg {
//  var count = new AtomicInteger(0)
//  def main(args: Array[String]): Unit = {
//    val pool: ExecutorService = Executors.newFixedThreadPool(16)
////    val path ="C:\\Users\\houya\\Desktop\\twice\\tst.txt"
//    val path ="C:\\Users\\houya\\Desktop\\twice\\uuphone_1_1w1.txt"
//
//    Source.fromFile(path).getLines().foreach(t => {
//      pool.execute(() => {
//        val resp = SendMsgUtil.sendMessage(Telphone(t), Array(), 328681, Env.OnlineEnv)
//        if (resp.result!=0){
//          print(resp,t)
//        }
//        println(count.getAndIncrement())
//      })
//    })
//    pool.shutdown()
//  }
}
