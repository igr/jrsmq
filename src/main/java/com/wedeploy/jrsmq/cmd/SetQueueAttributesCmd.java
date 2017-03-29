package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueAttributes;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.Q;
import static com.wedeploy.jrsmq.Values.UNSET_VALUE;

/**
 * Set queue parameters.
 * Note: At least one attribute (vt, delay, maxsize) must be supplied.
 * Only attributes that are supplied will be modified.
 */
public class SetQueueAttributesCmd extends BaseQueueCmd<QueueAttributes> {

	private String qname;
	private int vt = UNSET_VALUE;
	private int maxSize = UNSET_VALUE;
	private int delay = UNSET_VALUE;
	private final GetQueueAttributesCmd getQueueAttributes;

	public SetQueueAttributesCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
		this.getQueueAttributes = new GetQueueAttributesCmd(config, jedisSupplier);
	}

	/**
	 * The Queue name.
	 */
	public SetQueueAttributesCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * Optional length of time, in seconds, that a message received from a queue
	 * will be invisible to other receiving components when they ask to receive messages.
	 * Allowed values: 0-9999999 (around 115 days)
	 */
	public SetQueueAttributesCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	/**
	 * Optional maximum message size in bytes. Allowed values: 1024-65536 and -1 (for unlimited size).
	 */
	public SetQueueAttributesCmd maxsize(int maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	/**
	 * Optional The time in seconds that the delivery of all new messages in
	 * the queue will be delayed. Allowed values: 0-9999999 (around 115 days).
	 */
	public SetQueueAttributesCmd delay(int delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * @return {@link QueueAttributes}.
	 */
	@Override
	protected QueueAttributes exec(Jedis jedis) {
		Validator.create()
			.assertValidQname(qname)
			.assertAtLeastOneSet(vt, maxSize, delay);

		getQueue(jedis, qname, false); // just to check if it is an existing queue

		String key = config.redisNs() + qname + Q;

		List<String> times = jedis.time();

		Transaction tx = jedis.multi();

		tx.hset(key, "modified", times.get(0));

		Validator validator = Validator.create();

		if (vt != UNSET_VALUE) {
			validator.assertValidVt(vt);
			tx.hset(key, "vt", String.valueOf(vt));
		}
		if (maxSize != UNSET_VALUE) {
			validator.assertValidMaxSize(maxSize);
			tx.hset(key, "maxsize", String.valueOf(maxSize));
		}
		if (delay != UNSET_VALUE) {
			validator.assertValidDelay(delay);
			tx.hset(key, "delay", String.valueOf(delay));
		}

		tx.exec();

		return getQueueAttributes.qname(qname).exec();
	}
}