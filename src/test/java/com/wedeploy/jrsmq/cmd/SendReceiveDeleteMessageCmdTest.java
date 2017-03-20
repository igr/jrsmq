package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.QueueMessage;
import org.junit.Before;
import org.junit.Test;

import static com.wedeploy.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SendReceiveDeleteMessageCmdTest {

	@Before
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testDeleteMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").execute();
		assertNotNull(id);

		int deleted = rsmq.deleteMessage().qname(TEST_QNAME).id(id).execute();
		assertEquals(1, deleted);

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).execute();
		assertNull(msg);

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testDeleteMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		int deleted = rsmq.deleteMessage().qname(TEST_QNAME).id(Fixtures.NONEXISTING_ID).execute();
		assertEquals(0, deleted);

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").execute();
		assertNotNull(id);

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).execute();
		assertNotNull(msg);

		assertEquals("Hello World", msg.message());
		assertEquals(1, msg.rc());
		assertEquals(id, msg.id());

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage_twoMessages() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		String id1 = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World 1").execute();
		assertNotNull(id1);
		String id2 = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World 2").execute();
		assertNotNull(id2);

		QueueMessage msg1 = rsmq.receiveMessage().qname(TEST_QNAME).execute();
		assertNotNull(msg1);

		assertEquals("Hello World 1", msg1.message());
		assertEquals(1, msg1.rc());
		assertEquals(id1, msg1.id());

		QueueMessage msg2 = rsmq.receiveMessage().qname(TEST_QNAME).execute();
		assertNotNull(msg2);

		assertEquals("Hello World 2", msg2.message());
		assertEquals(1, msg2.rc());
		assertEquals(id2, msg2.id());

		assertTrue(msg1.sent() < msg2.sent());

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testReceiveMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).execute();
		assertNull(msg);

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

}
