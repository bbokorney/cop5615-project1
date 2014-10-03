package test

import java.io.File
import java.net.InetAddress

import akka.actor.Actor.Receive
import akka.actor.{Props, Actor, ActorSystem}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object test5 extends App {
  val map: java.util.Map[String, Object] = new java.util.HashMap[String, Object]
  map.put("akka.loglevel", "DEBUG")
  map.put("akka.remote.netty.tcp.hostname", "127.0.0.1")
  map.put("akka.remote.netty.tcp.port", "4321")
  val config = ConfigFactory.parseMap(map)
  println(config.root().render())
  val system = ActorSystem("Test", config)
  val testActor = system.actorOf(Props[TestActor2], "testActor")
  testActor ! "receive"
}

class TestActor2 extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case "receive" =>
      log.info("This is info")
      log.debug("This is debug")
  }
}




