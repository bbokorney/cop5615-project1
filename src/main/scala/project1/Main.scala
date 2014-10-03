package project1

import java.io.File
import java.net.InetAddress
import java.util
import akka.actor.{ActorIdentity, Identify, Props, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import com.typesafe.config.{ConfigFactory, Config}

object Main extends App {

  val parser = new scopt.OptionParser[AppConfig]("project1") {
    head("COP5615 Project 1 - Bitcoin Miner")
    help("help") text "Prints this help message"
    cmd("master") action { (x, c) =>
      c.copy(mode = "master")} text
        """Run as a master, allowing remote actors to connect and receive work.
          |Note that the master node still starts workers locally,
          |and thus can mine on its own.
        """.stripMargin children(
      opt[Long]("start") valueName "<start number>" optional() action { (x, c) =>
        c.copy(startValue = x)} text "Starting value to mine at",
      opt[Long]("end") valueName "<end number>" optional() action { (x, c) =>
        c.copy(endValue = x)} text "Ending value to mine until",
      opt[Long]("unit") valueName "<work unit size>" optional() action { (x, c) =>
        c.copy(workUnitSize = x)} text "Work unit size given per job to the workers",
      opt[Int]("workers") valueName "<num workers>" optional() action { (x, c) =>
        c.copy(numWorkers = x)} text "Number of workers to start locally",
      opt[File]("statlog") valueName "<filename>" optional() action { (x, c) =>
        c.copy(statLog = x)} text "Log of mining statistics",
      opt[Unit]("akkalog") optional() action { (x, c) =>
        c.copy(akkaLog = true)} text "Output a log of Akka events to akkalog.log",
      arg[Int]("<num zeros>") required() action { (x, c) =>
        c.copy(leadNumOfZeros = x)} text "The number of zeros in the prefix to search for"
      )
    cmd("slave") action { (x, c) =>
      c.copy(mode = "slave")} text "Run as a slave, receiving work from a remote master." children(
        opt[Int]("workers") valueName "<num workers>" optional() action { (x, c) =>
          c.copy(numWorkers = x)} text "Number of workers to start locally",
        opt[Unit]("akkalog") optional() action { (x, c) =>
          c.copy(akkaLog = true)} text "Output a log of Akka events to akkalog.log",
        opt[Int]("timeout") optional() action { (x, c) =>
          c.copy(masterPingTimeout = x)} text
            """Timeout in seconds to wait for replies
            |        from the master. If the timeout is exceeded,
            |        the slave will shut down.""".stripMargin,
        arg[String]("<master IP address>") required() action { (x, c) =>
          c.copy(masterIP = x)} text "IP address of the master node in the format X.X.X.X"
      )
    checkConfig { c =>
      if(c.mode == null) failure("Must specify master or slave mode") else success}
    checkConfig { c =>
      if(c.startValue >= c.endValue) failure("start must be less than end") else success}
  }

  def applyRemoteSettings(config: AppConfig, map: util.HashMap[String, Object]) = {
    map.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")
    map.put("akka.remote.netty.tcp.hostname", InetAddress.getLocalHost.getHostAddress)
    map.put("akka.remote.netty.tcp.port",
      if(config.masterIP == null) config.masterPort else "0")
  }

  def applyLogSettings(config: AppConfig, map: util.HashMap[String, Object]) = {
    map.put("akka.stdout-loglevel", "OFF")
    if(config.akkaLog) {
      val loggers = new java.util.LinkedList[String]
      loggers.add("akka.event.slf4j.Slf4jLogger")
      map.put("akka.loggers", loggers)
      map.put("akka.loglevel", "INFO")
    } else {
      map.put("akka.loglevel", "OFF")
    }
  }

  def createConfig(appConfig: AppConfig): Config = {
    val map = new java.util.HashMap[String, Object]

    applyRemoteSettings(appConfig, map)
    applyLogSettings(appConfig, map)

    ConfigFactory.parseMap(map)
  }

  def mainFunc: Unit = {
    parser.parse(args, AppConfig()) map { config =>
      val akkaConfig = createConfig(config)

      val system = ActorSystem("Miner", akkaConfig)
      val log = system.log

      val master = {
        if(config.mode == "master") {
          // this node is a master
          log.info("Starting local master")
          val statRecorder = system.actorOf(Props(new StatRecorder(config.statLog)), "statRecorder")
          val prefix = {
            var str = ""
            for(i <- 0 until config.leadNumOfZeros) {
              str += "0"
            }
            str
          }
          system.actorOf(Props(new Master(
            config.startValue, config.endValue, config.workUnitSize, prefix, statRecorder)), "master")
        }
        else {
          log.info("Looking up remote master")
          // look up a remote master
          implicit val timeout = Timeout(config.masterPingTimeout.seconds)
          val remotePath =
            "akka.tcp://Miner@%s:%s/user/master".format(config.masterIP, config.masterPort)
          log.info("Sending identify message")
          val remoteMaster = system.actorSelection(remotePath) ? Identify(remotePath)
          log.info("Message sent. Waiting for identity")
          val identity = Await.result(remoteMaster, config.masterPingTimeout.seconds)
            .asInstanceOf[ActorIdentity]
          log.info("Identity received: %s".format(identity.getRef))
          if(identity.getRef == null) {
            println("Unable to lookup remote master")
            system.shutdown()
            return
          }
          println("Successfully connected to remote master")
          identity.getRef
        }
      }
      log.info("Starting local boss")
      val boss = system.actorOf(Props(
        new Boss(master, config.numWorkers, config.masterPingTimeout)), "boss")
    } getOrElse {

    }
  }

  mainFunc
}

case class AppConfig(mode: String = null,
                     numWorkers: Int = Runtime.getRuntime.availableProcessors(),
                     masterIP: String = null,
                     masterPort: String = "4321",
                     statLog: File = null,
                     akkaLog: Boolean = false,
                     startValue: Long = 0,
                     endValue: Long = 10000000,
                     workUnitSize: Long = 10000,
                     leadNumOfZeros: Int = -1,
                     masterPingTimeout: Int = 5)


