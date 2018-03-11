package com.oblac.jrsmq.cmd;

import com.oblac.jrsmq.RedisSMQConfig;
import redis.clients.jedis.Jedis;

import java.util.Set;
import java.util.function.Supplier;

import static com.oblac.jrsmq.Values.QUEUES;

/**
 * List all queues.
 */
public class ListQueuesCmd extends BaseQueueCmd<Set<String>> {

	public ListQueuesCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * @return collection of all queue names.
	 */
	@Override
	protected Set<String> exec(Jedis jedis) {
		return jedis.smembers(config.redisNs() + QUEUES);
	}
}
