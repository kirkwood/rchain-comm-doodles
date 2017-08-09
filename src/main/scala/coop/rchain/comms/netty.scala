package coop.rchain.comm

class NettyComm(endpoint: Endpoint, peers: Array[Endpoint]) extends Comm {
  val senders: Array[Endpoint] = Array()

  override def send(data: Array[Byte]): Array[Result] = {
    senders map { s =>
      Error("Unimplemented")
    }
  }

  override def recv(): Result = {
    Error("Unimplemented")
  }
}
