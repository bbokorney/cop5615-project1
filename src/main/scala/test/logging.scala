package test

import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging

object logging extends App {
  val system = ActorSystem("TestSystem")
  val testActor = system.actorOf(Props[TestActor2])
  testActor ! "message"
}

class TestActor extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case "message" =>
      log.info("It's a message")
      log.info("It's another message")
      context.system.shutdown()
  }
}
