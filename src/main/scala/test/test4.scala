package test

import java.io.File

import akka.actor.{ActorSystem, Props}
import project1.{Boss, Master, StatRecorder}

object Main2 extends App {
  val system = ActorSystem("TestSystem")
  val statRecorder = system.actorOf(Props(new StatRecorder(new File("stats.txt"))), "statRecorder")
  val master = system.actorOf(Props(new Master(1, 1000000, 100000, "000", statRecorder)), "master")
  val boss = system.actorOf(Props(new Boss(master, 4, 10)), "boss")
}

