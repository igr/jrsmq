package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

import static com.wedeploy.jrsmq.Names.FR;
import static com.wedeploy.jrsmq.Names.Q;
import static com.wedeploy.jrsmq.Names.RC;
import static com.wedeploy.jrsmq.Util.toInt;

public class DeleteMessageCmd implements Cmd<Boolean> {

	private final RedisSMQConfig config;
	private final Jedis jedis;
	private String name;
	private String id;

	public DeleteMessageCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	public DeleteMessageCmd qname(String name) {
		this.name = name;
		return this;
	}

	public DeleteMessageCmd id(String id) {
		this.id = id;
		return this;
	}

	@Override
	public Boolean execute() {
		Validator.create()
			.assertValidQname(name)
			.assertValidId(id);

		String key = config.redisNs() + name;

		Transaction tx = jedis.multi();

		tx.zrem(key, id);
		tx.hdel(key + Q, id, id + RC, id + FR);

		List result = tx.exec();

		if (toInt(result, 0) == 1 && toInt(result, 1) > 0) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}
}