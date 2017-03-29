package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.Q;
import static com.wedeploy.jrsmq.Values.QUEUES;
import static com.wedeploy.jrsmq.Util.toInt;

/**
 * Create a new queue.
 */
public class CreateQueueCmd extends BaseQueueCmd<Integer> {

	private int vt = 30;
	private int delay = 0;
	private int maxsize = 65536;
	private String qname;

	public CreateQueueCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * The Queue name. Maximum 160 characters; alphanumeric characters, hyphens (-), and underscores (_) are allowed.
	 */
	public CreateQueueCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * Optional (Default: 30) length of time, in seconds, that a message received from a
	 * queue will be invisible to other receiving components when they ask to receive messages. Allowed values: 0-9999999 (around 115 days).
	 */
	public CreateQueueCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	/**
	 * Optional (Default: 0) time in seconds that the delivery of all new messages in the queue will be delayed.
	 * Allowed values: 0-9999999 (around 115 days)
	 */
	public CreateQueueCmd delay(int delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * Optional (Default: 65536) maximum message size in bytes. Allowed values: 1024-65536 and -1 (for unlimited size).
	 */
	public CreateQueueCmd maxsize(int maxsize) {
		this.maxsize = maxsize;
		return this;
	}

	/**
	 * @return 1
	 */
	@Override
	protected Integer exec(Jedis jedis) {
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
