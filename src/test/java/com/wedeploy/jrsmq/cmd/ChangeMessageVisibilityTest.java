package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Fixtures;
import org.junit.Before;
import org.junit.Test;

import static com.wedeploy.jrsmq.Fixtures.TEST_QNAME;
import static org.junit.Assert.assertEquals;

public class ChangeMessageVisibilityTest {

	@Before
	public void setUp() {
		Fixtures.cleanup();
	}

	@Test
	public void testChangeMessageVisibility_noMessage() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		int result = rsmq.changeMessageVisibility().qname(TEST_QNAME).id(Fixtures.NONEXISTING_ID).execute();

		assertEquals(0, result);

		// clean up

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}

	@Test
	public void testChangeMessageVisibility() {
		Fixtures.TestRedisSMQ rsmq = Fixtures.redisSMQ();

		rsmq.connect().createQueue().qname(TEST_QNAME).execute();

		String id = rsmq.sendMessage().qname(TEST_QNAME).message("Hello World").execute();

		int result = rsmq.changeMessageVisibility().qname(TEST_QNAME).id(id).vt(1000).execute();

		assertEquals(1, result);

		// clean up

		rsmq.deleteQueue().qname(TEST_QNAME).execute();
		rsmq.quit();
	}
}
