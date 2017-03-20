![RSMQ: Redis Simple Message Queue for Node.js](https://img.webmart.de/rsmq_wide.png)

# Redis Simple Message Queue

A lightweight message queue for Java that requires no dedicated queue server. Just a Redis server.

Java implementation of https://github.com/smrchy/rsmq.

## Java implementation notes

We are trying to follow the javascript contract as much as possible, including
the method and properties naming.

The only difference is the method `connect()` that does not exist in JavaScript
library. We decided to add one to make the usage lifecycle bit more flexible.

## Example

		RedisSMQ rsmq = new RedisSMQ();

		rsmq.connect()
			.createQueue()
			.qname("myqueue")
			.execute();

		String id = rsmq.sendMessage()
						.qname("myqueue")
						.message("Hello World")
						.execute();
	
		QueueMessage msg = rsmq.receiveMessage()
								.qname("myqueue")
								.execute();

		rsmq.deleteQueue()
			.qname("myqueue")
			.execute();
			
		rsmq.quit();

Enjoy!