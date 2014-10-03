package project1

import java.util.concurrent.TimeoutException

import akka.actor._
import akka.event.Logging
import akka.routing._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Boss(master: ActorRef, numWorkers: Int, masterPingTimeout: Int) extends Actor {
  val log = Logging(context.system, this)

  master ! NeedWork(numWorkers * 2)
  context.system.actorOf(Props(new Pinger(master, masterPingTimeout)))
  var router = {
    val workers = Vector.fill(numWorkers) {
      createWorker
    }
    Router(SmallestMailboxRoutingLogic(), workers)
  }

  override def receive: Receive = {
    case Terminated(worker) =>
      log.info("Received terminate from %s, starting new worker".format(worker))
      router = router.removeRoutee(worker)
      router = router.addRoutee(createWorker)
    case DoJobs(jobs) =>
      log.info("Received DoJobs: %s".format(jobs))
      jobs.foreach(job => router.route(DoWork(job), context.self))
    case WorkComplete(result) =>
      log.info("Received WorkComplete: %s".format(result))
      master ! WorkComplete(result)
      log.info("Requesting more work")
      master ! NeedWork(1)
  }

  def createWorker: ActorRefRoutee = {
    val worker = context.actorOf(Props[Worker])
    context watch worker
    ActorRefRoutee(worker)
  }
}

class Pinger(master: ActorRef, masterPingTimeout: Int) extends Actor {
  implicit val timeout = new Timeout(masterPingTimeout.seconds)
  self ! PingMaster
  override def receive: Actor.Receive = {
    case PingMaster =>
      try {
        val future = master ? Ping
        val reply = Await.result(future, masterPingTimeout.seconds)
        context.system.scheduler.scheduleOnce(5.seconds)(self ! PingMaster)
      } catch {
        case e: TimeoutException =>
          println("Ping request to master timed out")
          context.system.shutdown()
      }
  }
}
