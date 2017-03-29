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

		rsmq.createQueue().qname(TEST_QNAME).exec();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").exec();
		assertNotNull(id);

		int deleted = rsmq.deleteMessage().qname(TEST_QNAME).id(id).exec();
		assertEquals(1, deleted);

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).exec();
		assertNull(msg);

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testDeleteMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		int deleted = rsmq.deleteMessage().qname(TEST_QNAME).id(Fixtures.NONEXISTING_ID).exec();
		assertEquals(0, deleted);

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").exec();
		assertNotNull(id);

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).exec();
		assertNotNull(msg);

		assertEquals("Hello World", msg.message());
		assertEquals(1, msg.rc());
		assertEquals(id, msg.id());

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testSendReceiveMessage_twoMessages() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		String id1 = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World 1").exec();
		assertNotNull(id1);
		String id2 = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World 2").exec();
		assertNotNull(id2);

		QueueMessage msg1 = rsmq.receiveMessage().qname(TEST_QNAME).exec();
		assertNotNull(msg1);

		assertEquals("Hello World 1", msg1.message());
		assertEquals(1, msg1.rc());
		assertEquals(id1, msg1.id());

		QueueMessage msg2 = rsmq.receiveMessage().qname(TEST_QNAME).exec();
		assertNotNull(msg2);

		assertEquals("Hello World 2", msg2.message());
		assertEquals(1, msg2.rc());
		assertEquals(id2, msg2.id());

		assertTrue(msg1.sent() <= msg2.sent());

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testReceiveMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		QueueMessage msg = rsmq.receiveMessage().qname(TEST_QNAME).exec();
		assertNull(msg);

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

}
