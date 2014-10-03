package project1

import java.io.{FileWriter, BufferedWriter, File}

import akka.actor.Actor
import akka.event.Logging

import scala.collection.mutable.ListBuffer

class StatRecorder(file: File) extends Actor {
  val log = Logging(context.system, this)
  val statLog = if(file == null) null else new BufferedWriter(new FileWriter(file))

  def logStats(job: Job, address: String, startTime: Long, endTime: Long, coins: ListBuffer[Coin]): Unit = {
    statLog.write("%s\t%s\t%s\t%s\t%s\t".format(job.start, job.end, address, startTime, endTime))
    coins.foreach(coin => statLog.write("%s,%s\t".format(coin.input, coin.output)))
    statLog.newLine()
    statLog.flush()
  }

  override def receive: Receive = {
    case ProcessResult(Result(job, address, startTime, endTime, coins)) =>
      log.info("Processing results: %s %s %s %s %s".format(job, address, startTime, endTime, coins))
      printCoins(coins)
      if(statLog != null) {
        logStats(job, address, startTime, endTime, coins)
      }
  }

  def printCoins(coins: scala.collection.mutable.ListBuffer[Coin]): Unit = {
    coins.foreach(coin => println("%s\t%s".format(coin.input, coin.output)))
  }
}
