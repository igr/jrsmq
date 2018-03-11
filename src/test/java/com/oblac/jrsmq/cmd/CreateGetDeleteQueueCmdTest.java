package com.oblac.jrsmq.cmd;

import com.oblac.jrsmq.Fixtures;
import com.oblac.jrsmq.Fixtures.TestRedisSMQ;
import com.oblac.jrsmq.QueueDef;
import com.oblac.jrsmq.RedisSMQException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.oblac.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CreateGetDeleteQueueCmdTest {

	@BeforeEach
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testCreateQueue() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();

		int result = rsmq.createQueue().qname(TEST_QNAME).exec();

		assertEquals(1, result);

		QueueDef queueDef = Fixtures.getQueue(rsmq, TEST_QNAME);

		assertEquals(TEST_QNAME, queueDef.qname());
		assertEquals(65536, queueDef.maxsize());

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testCreateQueue_existingName() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.createQueue().qname(TEST_QNAME).exec();

		try {
			rsmq.createQueue().qname(TEST_QNAME).exec();
			fail("");
		}
		catch (RedisSMQException e) {
			// ignore
		}

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).exec();
		rsmq.quit();
	}

	@Test
	public void testDeleteQueue_noQueue() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		try {
			rsmq.deleteQueue().qname("nonexistingqueue").exec();
			fail("");
		}
		catch (Exception ex) {
			// ignore
		}

		rsmq.quit();
	}

	@Test
	public void testGetQueue_missingQueue() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		try {
			Fixtures.getQueue(rsmq, "nonexisting");
			fail("");
		}
		catch (Exception e) {
			// ignore
		}

		rsmq.quit();
	}
}
