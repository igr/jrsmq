package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

import static com.wedeploy.jrsmq.Names.Q;
import static com.wedeploy.jrsmq.Names.QUEUES;
import static com.wedeploy.jrsmq.Util.toInt;

public class CreateQueueCmd implements Cmd<Integer> {

	private final Jedis jedis;
	private final RedisSMQConfig config;
	private int vt = 30;
	private int delay = 0;
	private int maxsize = 65536;
	private String qname;

	public CreateQueueCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	public CreateQueueCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	public CreateQueueCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	public CreateQueueCmd delay(int delay) {
		this.delay = delay;
		return this;
	}

	public CreateQueueCmd maxSize(int maxsize) {
		this.maxsize = maxsize;
		return this;
	}

	@Override
	public Integer execute() {
		Validator.create()
			.assertValidQname(qname)
			.assertValidVt(vt)
			.assertValidDelay(delay)
			.assertValidMaxSize(maxsize);

		List<String> times = jedis.time();

		Transaction tx = jedis.multi();

		String key = config.redisNs() + qname + Q;

		tx.hsetnx(key, "vt", String.valueOf(vt));
		tx.hsetnx(key, "delay", String.valueOf(delay));
		tx.hsetnx(key, "maxsize", String.valueOf(maxsize));
		tx.hsetnx(key, "created", times.get(0));
		tx.hsetnx(key, "modified", times.get(0));

		List results = tx.exec();

		int createdCount = toInt(results, 0);

		if (createdCount == 0) {
			throw new RedisSMQException("Queue already exists: " + qname);
		}

		jedis.sadd(config.redisNs() + QUEUES, qname);

		return createdCount;
	}
}
