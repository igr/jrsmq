package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import com.wedeploy.jrsmq.Fixtures.TestRedisSMQ;
import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.fail;

public class CreateGetDeleteQueueCmdTest {

	@Test
	public void testCreateQueue() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		QueueDef queueDef = Fixtures.getQueue(rsmq, qname);

		Assert.assertEquals(qname, queueDef.qname());
		Assert.assertEquals(65536, queueDef.maxsize());

		// cleanup

		rsmq.deleteQueue().qname(qname).execute();
		rsmq.quit();
	}

	@Test
	public void testCreateQueue_existingName() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();
		String qname = "testqueue";

		rsmq.connect().createQueue().qname(qname).execute();

		try {
			rsmq.connect().createQueue().qname(qname).execute();
			fail();
		}
		catch (RedisSMQException e) {
			// ignore
		}

		// cleanup

		rsmq.deleteQueue().qname(qname).execute();
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
