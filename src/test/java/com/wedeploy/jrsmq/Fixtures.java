package com.wedeploy.jrsmq;

import com.wedeploy.jrsmq.cmd.BaseQueueCmd;
import redis.clients.jedis.Jedis;

public class Fixtures {

	public static final String TEST_QNAME = "testqueue";
	public static final String NONEXISTING_ID = "12345678901234567890123456789012";

	public static void cleanup() {
		TestRedisSMQ rsmq = Fixtures.redisSMQ();
		try {
			rsmq.deleteQueue().qname(TEST_QNAME).exec();
		}
		catch (Exception ignore) {
		}
		finally {
			rsmq.quit();
		}
	}

	/**
	 * Returns test configuration.
	 */
	public static RedisSMQConfig testConfig() {
		return new RedisSMQConfig().ns("trsmq");
	}

	public static TestRedisSMQ redisSMQ() {
		return new TestRedisSMQ(testConfig());
	}

	public static QueueDef getQueue(TestRedisSMQ redisSMQ, String name) {
		return new BaseQueueCmd<QueueDef>(redisSMQ.config(), redisSMQ.jedis()) {
			@Override
			public QueueDef exec() {
				return getQueue(name, true);
			}
		}.exec();
	}

	/**
	 * Test wrapper of RedisSMQ.
	 */
	public static class TestRedisSMQ extends RedisSMQ {
		public TestRedisSMQ(RedisSMQConfig config) {
			super(config);
		}

		public RedisSMQConfig config() {
			return config;
		}
	}
}
