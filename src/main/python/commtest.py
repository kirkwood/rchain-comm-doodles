#!/usr/bin/env python

import sys, time, signal
import zmq
import uuid
from threading import Thread, Event

ctx = zmq.Context()
my_id = uuid.uuid4()

ports = [ 19900, 19901, 19902, 19903 ]

def setup_sockets(idx):
    me = ports[idx]
    they = list(ports)
    del they[idx]

    my_addy = "tcp://*:%d" % me
    they_addy = [ "tcp://localhost:%d" % x for x in they ]

    pushes = [ ctx.socket(zmq.PUSH) for _ in they ]
    for i in range(len(pushes)):
        pushes[i].connect(they_addy[i])

    pull = ctx.socket(zmq.PULL)
    pull.bind(my_addy)

    return (pull, pushes)

def do_send(socks, e):
    i = 0
    while not e.wait(1):
        for s in socks:
            msg = "%s - %d" % (my_id, i)
            s.send(msg, flags = zmq.DONTWAIT)
            print 'Sent ' + msg
        i += 1

def do_recv(sock, e):
    while not e.wait(0.5):
        try:
            v = sock.recv(flags = zmq.DONTWAIT)
            print 'Received ' + v
        except zmq.error.Again as ex:
            pass

stopping = Event()

def handle_sigint(signo, frame):
    print 'Handling signal...',
    stopping.set()
    print 'Set event'

if __name__ == '__main__':
    signal.signal(signal.SIGINT, handle_sigint)
    print 'Set signal handler'

    (recv, send) = setup_sockets(int(sys.argv[1]))
    print recv, send
    sender = Thread(target = do_send, args = (send, stopping))
    receiver = Thread(target = do_recv, args = (recv, stopping))

    sender.start()
    receiver.start()

    stopping.wait()

    sender.join()
    receiver.join()
