package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.QueueMessage;
import org.junit.Before;
import org.junit.Test;

import static com.wedeploy.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PopMessageTest {

	@Before
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testPopMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").exec();
		assertNotNull(id);

		QueueMessage msg = rsmq.popMessage().qname(TEST_QNAME).exec();
		assertNotNull(msg);

		assertEquals("Hello World", msg.message());
		assertEquals(1, msg.rc());
		assertEquals(id, msg.id());

		rsmq.receiveMessage().qname(TEST_QNAME).exec();

		msg = rsmq.popMessage().qname(TEST_QNAME).exec();
		assertNull(msg);

		// clean up

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}


	@Test
	public void testPopMessage_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		QueueMessage msg = rsmq.popMessage().qname(TEST_QNAME).exec();
		assertNull(msg);

		// clean up

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}
}
