package com.wedeploy.jrsmq;

import com.wedeploy.jrsmq.cmd.BaseQueueCmd;
import redis.clients.jedis.Jedis;

public class Fixtures {

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
			public QueueDef execute() {
				return getQueue(name, true);
			}
		}.execute();
	}

	/**
	 * Test wrapper of RedisSMQ.
	 */
	public static class TestRedisSMQ extends RedisSMQ {
		public TestRedisSMQ(RedisSMQConfig config) {
			super(config);
		}

		public Jedis jedis() {
			return jedis;
		}
		public RedisSMQConfig config() {
			return config;
		}
	}
}
