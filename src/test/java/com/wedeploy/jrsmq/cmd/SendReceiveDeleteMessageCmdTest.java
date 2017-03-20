package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.QueueMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SendReceiveDeleteMessageCmdTest {

	@Test
	public void testDeleteMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		String id = rsmq.sendMessage().qname(qname).message("Hello World").execute();
		assertNotNull(id);

		boolean deleted = rsmq.deleteMessage().qname(qname).id(id).execute();
		assertTrue(deleted);

		QueueMessage msg = rsmq.receiveMessage().qname(qname).execute();
		assertNull(msg);

		rsmq.deleteQueue().qname(qname).execute();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		String id = rsmq.sendMessage().qname(qname).message("Hello World").execute();
		assertNotNull(id);

		QueueMessage msg = rsmq.receiveMessage().qname(qname).execute();
		assertNotNull(msg);

		assertEquals("Hello World", msg.message());
		assertEquals(1, msg.rc());
		assertEquals(id, msg.id());

		rsmq.deleteQueue().qname(qname).execute();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage_twoMessages() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		String id1 = rsmq.sendMessage().qname(qname).message("Hello World 1").execute();
		assertNotNull(id1);
		String id2 = rsmq.sendMessage().qname(qname).message("Hello World 2").execute();
		assertNotNull(id2);

		QueueMessage msg1 = rsmq.receiveMessage().qname(qname).execute();
		assertNotNull(msg1);

		assertEquals("Hello World 1", msg1.message());
		assertEquals(1, msg1.rc());
		assertEquals(id1, msg1.id());

		QueueMessage msg2 = rsmq.receiveMessage().qname(qname).execute();
		assertNotNull(msg2);

		assertEquals("Hello World 2", msg2.message());
		assertEquals(1, msg2.rc());
		assertEquals(id2, msg2.id());

		assertTrue(msg1.sent() < msg2.sent());

		rsmq.deleteQueue().qname(qname).execute();
		rsmq.quit();
	}

	@Test
	public void testReceiveMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		QueueMessage msg = rsmq.receiveMessage().qname(qname).execute();
		assertNull(msg);

		rsmq.deleteQueue().qname(qname).execute();
		rsmq.quit();
	}

}
