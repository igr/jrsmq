package com.oblac.jrsmq.cmd;

import com.oblac.jrsmq.Fixtures;
import com.oblac.jrsmq.QueueAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oblac.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class QueueAttributesTest {

	@BeforeEach
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testGetQueueAttributes() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		rsmq.sendMessage().qname(TEST_QNAME).message("Hello1").exec();
		rsmq.receiveMessage().qname(TEST_QNAME).exec();
		rsmq.sendMessage().qname(TEST_QNAME).message("Hello2").exec();

		QueueAttributes qa = rsmq.getQueueAttributes().qname(TEST_QNAME).exec();

		assertNotNull(qa);
		assertEquals(0, qa.delay());
		assertEquals(2, qa.totalSent());
		assertEquals(1, qa.totalRecv());
		assertEquals(2, qa.msgs());
		assertEquals(2, qa.hiddenMsgs());

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testSetQueueAttributes_noChange() throws InterruptedException {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		try {
			rsmq.setQueueAttributes().qname(TEST_QNAME).exec();
			fail("");
		}
		catch (Exception ignore) {}
		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testSetQueueAttributes() throws InterruptedException {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		Thread.sleep(1000);

		QueueAttributes qa = rsmq.setQueueAttributes().qname(TEST_QNAME).delay(100).exec();

		assertEquals(100, qa.delay());
		assertTrue(qa.modified() > qa.created());

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}
}
