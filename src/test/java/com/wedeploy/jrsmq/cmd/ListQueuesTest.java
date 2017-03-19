package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ListQueuesTest {

	@Test
	public void testListQueues_noQueues() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		rsmq.connect();

		Set<String> queues = rsmq.listQueues().execute();
		assertTrue(queues.isEmpty());

		rsmq.quit();
	}

	@Test
	public void testListQueues() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		rsmq.connect().createQueue().withName("one").execute();

		Set<String> queues = rsmq.listQueues().execute();
		assertEquals(1, queues.size());

		String name = queues.iterator().next();
		assertEquals("one", name);

		rsmq.deleteQueue().withName("one").execute();
		rsmq.quit();
	}

}
