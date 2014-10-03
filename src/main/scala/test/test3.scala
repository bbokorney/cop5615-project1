package test

import akka.actor._
import akka.event.Logging
import project1._

import scala.collection.mutable

class TestMaster extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case NeedWork(numJobs) =>
      log.info("Received NeedWork: %s jobs".format(numJobs))
      val jobs = new mutable.ArrayBuffer[Job](numJobs)
      val step = 10
      for(i <- Seq.range[Long](0, 80, step)) {
        jobs += new Job("[%s,%s)".format(i, i+step), i, i+step, "000")
      }
      sender ! DoJobs(jobs)
    case WorkComplete(Result(job, localAddress, startTime, endTime, coins)) =>
      log.info("Job completed: %s, %s, %s, %s, %s".format(job, localAddress, startTime, endTime, coins))
  }
}

object Main extends App {
  val system = ActorSystem("TestSystem")
  val master = system.actorOf(Props[TestMaster], "master")
//  val boss = system.actorOf(Props(classOf[Boss], master, 4), "boss")
  val boss = system.actorOf(Props(new Boss(master, 4, 10)), "boss")
}


