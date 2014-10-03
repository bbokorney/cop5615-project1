package project1

import akka.actor.{ActorRef, Actor}
import akka.event.Logging

import scala.collection.mutable
import scala.util.control.Breaks._

class Master(startValue: Long, endValue: Long, workUnitSize: Long, prefix: String, statRecorder: ActorRef) extends Actor {
  val log = Logging(context.system, this)
  var currentValue = startValue
  var jobsSent = 0
  var jobsCompleted = 0

  override def receive: Receive = {
    case NeedWork(numJobs) =>
      log.info("Received NeedWork: %s jobs".format(numJobs))
      if(currentValue < endValue) {
        val jobs = createJobs(numJobs)
        sender ! DoJobs(jobs)
        jobsSent += jobs.length
      }
    case WorkComplete(Result(job, address, startTime, endTime, coins)) =>
      log.info("Job completed: %s %s %s %s %s".format(job, address, startTime, endTime, coins))
      statRecorder ! ProcessResult(Result(job, address, startTime, endTime, coins))
      jobsCompleted += 1
      if(jobsSent == jobsCompleted) {
        context.system.shutdown()
      }
    case Ping =>
      sender ! Pong
  }

  def createJobs(numJobs: Int): mutable.ArrayBuffer[Job] = {
    val jobs = new  mutable.ArrayBuffer[Job](numJobs)
    breakable { for(i <- 0 until numJobs) {
      if(currentValue >= endValue) {
        break
      }
      val startVal = currentValue
      val endVal = currentValue + Math.min(workUnitSize, endValue - startValue)
      jobs += Job(createJobId(startVal, endVal), startVal, endVal, prefix)
      currentValue += workUnitSize
    } }
    jobs
  }

  def createJobId(start: Long, end: Long) = "[%s,%s)".format(start, end)
}
