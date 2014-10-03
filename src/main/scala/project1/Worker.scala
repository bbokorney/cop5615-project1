package project1

import java.math.BigInteger
import java.net.InetAddress

import akka.actor.Actor
import akka.event.Logging
import org.apache.commons.codec.binary.Base64
import scala.language.postfixOps
import com.roundeights.hasher.Implicits._

import scala.compat.Platform

class Worker extends Actor {
  val log = Logging(context.system, this)
  val localAddress = InetAddress.getLocalHost.toString

  override def receive: Receive = {
    case DoWork(job) =>
      log.info("Received do work: %s".format(job))
      val result = doJob(job)
      log.info("Done working, sending result: %s".format(result))
      sender ! WorkComplete(result)
  }

  def doJob(job: Job): Result = {
    val coins = scala.collection.mutable.ListBuffer[Coin]()
    val startTime = Platform.currentTime
    for(data <- job.start until job.end) {
      val tuple = hash(data)
      if(checkForCoin(tuple._2, job.prefix)) {
        coins += Coin(tuple._1, tuple._2)
      }
    }
    val endTime = Platform.currentTime
    Result(job, localAddress, startTime, endTime, coins)
  }

  def checkForCoin(potentialCoin: String, prefix: String): Boolean = {
    if(potentialCoin.startsWith(prefix)) {
      return true
    }
    false
  }

  def hash(num: Long): (String, String) = {
    val value = "bbokorney" + b64EncodeLong(num)
    val hashed = value.sha256.hex
    (value, hashed)
  }

  def b64EncodeLong(num: Long): String = {
    new String(Base64.encodeInteger(BigInteger.valueOf(num)))
  }
}
