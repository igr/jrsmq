package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.Fixtures.TestRedisSMQ;
import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.wedeploy.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.Assert.fail;

public class CreateGetDeleteQueueCmdTest {

	@Before
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testCreateQueue() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		QueueDef queueDef = Fixtures.getQueue(rsmq, TEST_QNAME);

		Assert.assertEquals(TEST_QNAME, queueDef.qname());
		Assert.assertEquals(65536, queueDef.maxsize());

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testCreateQueue_existingName() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		try {
			rsmq.connect().createQueue().qname(TEST_QNAME).execute();
			fail();
		}
		catch (RedisSMQException e) {
			// ignore
		}

		// cleanup

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testDeleteQueue_noQueue() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		try {
			rsmq.connect().deleteQueue().qname("nonexistingqueue").execute();
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

		rsmq.connect();

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
