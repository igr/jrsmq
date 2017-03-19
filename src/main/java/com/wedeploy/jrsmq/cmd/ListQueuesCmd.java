package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import redis.clients.jedis.Jedis;

import java.util.Set;

import static com.wedeploy.jrsmq.Names.QUEUES;

public class ListQueuesCmd implements Cmd<Set<String>> {

	private final RedisSMQConfig config;
	private final Jedis jedis;

	public ListQueuesCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	@Override
	public Set<String> execute() {
		return jedis.smembers(config.getRedisNs() + QUEUES);
	}
}
