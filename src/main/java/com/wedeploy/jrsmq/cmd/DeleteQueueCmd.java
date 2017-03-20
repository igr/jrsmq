package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Names;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

import static com.wedeploy.jrsmq.Names.QUEUES;
import static com.wedeploy.jrsmq.Util.toInt;

public class DeleteQueueCmd implements Cmd<Void> {

	private final RedisSMQConfig config;
	private final Jedis jedis;
	private String qname;

	public DeleteQueueCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	public DeleteQueueCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	@Override
	public Void execute() {
		Validator.create()
			.assertValidQname(qname);

		String key = config.redisNs() + qname;

		Transaction tx = jedis.multi();

		tx.del(key + Names.Q);
		tx.del(key);
		tx.srem(config.redisNs() + QUEUES, qname);

		List result = tx.exec();

		if (toInt(result, 0) == 0) {
			throw new RedisSMQException("Queue not found: " + qname);
		}

		return null;
	}


}
