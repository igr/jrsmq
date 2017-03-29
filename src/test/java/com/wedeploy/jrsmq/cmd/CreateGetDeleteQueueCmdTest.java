package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.Fixtures.TestRedisSMQ;
import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.wedeploy.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CreateGetDeleteQueueCmdTest {

	@Before
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testCreateQueue() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();

		int result = rsmq.createQueue().qname(TEST_QNAME).exec();

		assertEquals(1, result);

		QueueDef queueDef = Fixtures.getQueue(rsmq, TEST_QNAME);

		Assert.assertEquals(TEST_QNAME, queueDef.qname());
		Assert.assertEquals(65536, queueDef.maxsize());

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
			fail();
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
			fail();
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
			fail();
		}
		catch (Exception e) {
			// ignore
		}

		rsmq.quit();
	}
}
