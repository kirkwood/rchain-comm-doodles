package coop.rchain.comm

import org.rogach.scallop._
import java.util.UUID

object Defaults {
  val listenPort = 44444
  val transport = Some("zeromq")
  val listen = Some(s"*:$listenPort")
  val httpPort = Some(8878)
}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {
  version("0.0.1 rchain blah blah")

  // These keywords denote implemented transports
  val validTransports = Set("zeromq", "netty")

  val transport = opt[String](
    default = Defaults.transport,
    short = 't',
    descr = "Transport mechanism to use; one of: " + (validTransports mkString ", "))

  validate(transport) { t =>
    if (validTransports contains t) Right(Unit)
    else Left(s"Bad transport: $t (must be one of: " + (validTransports mkString ", ") + ")")
  }

  val listen = opt[String](
    default = Defaults.listen,
    short = 'p',
    descr = "Address (host:port) on which transport should listen.")

  // Defer analysis of this value until later; it's hard to verify
  // statically
  val peers = opt[String](
    required = true,
    default = None,
    descr = "Comma-separated list of peer nodes in host:port format.")

  val httpPort = opt[Int](
    default = Defaults.httpPort,
    validate = (0 <),
    short = 'H',
    descr = "Port on which HTTP server should listen.")

  verify()
}

object CommTest {
  def makeEndpoint(spec: String): Endpoint = {
    EndpointFactory.fromString(spec, defaultPort = Defaults.listenPort)
  }

  def main(args: Array[String]) {
    val conf = new Conf(args)

    val listen = makeEndpoint(conf.listen())

    val peers = (conf.peers() split ",")
      .filter { x => x != "" }
      .map { x => makeEndpoint(x) }

    peers foreach { x => println("peer: " + x) }

    println(conf.summary)

    val me = UUID.randomUUID
    println(s"I am $me")

    val zmq = new ZeromqComm(listen)

    for (i <- 1 to 10) {
      Thread.sleep(1000)
      println(
        (zmq send (s"ME: $me ($i)" getBytes)) match {
          case Response(d) => s"data: $d: ‘" + new String(d) + "’"
          case Error(msg) => s"error: $msg"
        }
      )
    }
  }
}
