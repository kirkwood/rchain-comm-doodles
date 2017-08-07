package coop.rchain.comm

import org.zeromq.ZMQ

class Endpoint(host: String, port: Int) {
  override def toString = s"#{Endpoint $host:$port}"
  def format = s"$host:$port"
}

object EndpointFactory {
  def fromString(hostport: String, defaultHost: String = "*", defaultPort: Int = 44444): Endpoint = {
    val stuff = hostport split ":"
    stuff.size match {
      case 1 =>
        new Endpoint(stuff(0), defaultPort)
      case 2 =>
        if (stuff(0) == "") new Endpoint("localhost", stuff(1) toInt)
        else new Endpoint(stuff(0), stuff(1) toInt)
    }
  }
}

trait Result

case class Response(data: Array[Byte]) extends Result
case class Error(message: String) extends Result

trait Comm {
  def send(data: Array[Byte]): Result
  // def recv(): Result
}

object ZeromqComm {
  lazy val context = {
    ZMQ.context(1)
  }
}

class ZeromqComm(endpoint: Endpoint) extends Comm {

  lazy val sock = {
    val uri = s"tcp://${endpoint format}"
    val s = ZeromqComm.context.socket(ZMQ.PUSH)
    s.bind(uri)
    println(s"Bound sock $uri.")
    s
  }

  override def send(data: Array[Byte]): Result = {
    sock.send(data, ZMQ.DONTWAIT) match {
      case false => Error("Couldn't send")
      case _ => Response((s"Sent $data: " getBytes) ++ data)
    }
  }
}
