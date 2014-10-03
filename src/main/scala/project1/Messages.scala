package project1

import scala.collection.mutable.ArrayBuffer

trait Message

case class NeedWork(numJobs: Int = 1) extends Message
case class DoJobs(job: ArrayBuffer[Job]) extends Message
case class DoWork(job: Job) extends Message
case class WorkComplete(result: Result) extends Message
case class ProcessResult(result: Result) extends Message
case object PingMaster extends Message
case object Ping extends Message
case object Pong extends Message

case class Job(id: String, start: Long, end: Long, prefix: String)
case class Coin(input: String, output: String)
case class Result(job: Job, address: String, startTime: Long, endTime: Long, coins: scala.collection.mutable.ListBuffer[Coin])
