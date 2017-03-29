package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.Q;

/**
 * Send a new message.
 */
public class SendMessageCmd extends BaseQueueCmd<String> {

	private String qname;
	private String message;
	private int delay;

	public SendMessageCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * The Queue name.
	 */
	public SendMessageCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * The message's contents.
	 */
	public SendMessageCmd message(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Optional (Default: queue settings) time in seconds that the delivery of
	 * the message will be delayed. Allowed values: 0-9999999 (around 115 days)
	 */
	public SendMessageCmd delay(int delay) {
		this.delay = delay;
		return this;
	}

	/**
	 * @return The internal message id.
	 */
	@Override
	protected String exec(Jedis jedis) {
		QueueDef q = getQueue(jedis, qname, true);

		Validator.create()
			.assertValidQname(qname)
			.assertValidDelay(delay)
			.assertValidMessage(q, message);

		Transaction tx = jedis.multi();

		String key = config.redisNs() + qname + Q;

		tx.zadd(config.redisNs() + qname, q.ts() + delay * 1000, q.uid());
		tx.hset(key, q.uid(), message);
		tx.hincrBy(key, "totalsent", 1);

		tx.exec();

		return q.uid();
	}
}
