![RSMQ: Redis Simple Message Queue for Node.js](https://img.webmart.de/rsmq_wide.png)

# Redis Simple Message Queue

A lightweight message queue for Java that requires no dedicated queue server. Just a Redis server.

Java implementation of https://github.com/smrchy/rsmq.

## Version

	com.oblac:jrsmq:1.2.0

## Java implementation notes

We are trying to follow the javascript contract as much as possible, including
the method and properties naming.

## Example

    RedisSMQ rsmq = new RedisSMQ();

    rsmq.createQueue()
        .qname("myqueue")
        .exec();

    String id = rsmq.sendMessage()
                    .qname("myqueue")
                    .message("Hello World")
                    .exec();

    QueueMessage msg = rsmq.receiveMessage()
                            .qname("myqueue")
                            .exec();

    rsmq.deleteQueue()
        .qname("myqueue")
        .exec();

    rsmq.quit();

Enjoy!
