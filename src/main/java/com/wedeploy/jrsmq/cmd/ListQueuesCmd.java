package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import redis.clients.jedis.Jedis;

import java.util.Set;

import static com.wedeploy.jrsmq.Names.QUEUES;

/**
 * List all queues.
 */
public class ListQueuesCmd implements Cmd<Set<String>> {

	private final RedisSMQConfig config;
	private final Jedis jedis;

	public ListQueuesCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	/**
	 * @return collection of all queue names.
	 */
	@Override
	public Set<String> execute() {
		return jedis.smembers(config.redisNs() + QUEUES);
	}
}
