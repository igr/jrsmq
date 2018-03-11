package com.oblac.jrsmq.cmd;

import com.oblac.jrsmq.Fixtures;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListQueuesTest {

	@Test
	public void testListQueues_noQueues() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		Set<String> queues = rsmq.listQueues().exec();
		assertTrue(queues.isEmpty());

		rsmq.quit();
	}

	@Test
	public void testListQueues() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();
		rsmq.createQueue().qname("one").exec();

		Set<String> queues = rsmq.listQueues().exec();
		assertEquals(1, queues.size());

		String name = queues.iterator().next();
		assertEquals("one", name);

		rsmq.deleteQueue().qname("one").exec();
		rsmq.quit();
	}

}
