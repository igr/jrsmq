package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.Q;
import static com.wedeploy.jrsmq.Util.toInt;

/**
 * Delete a message.
 */
public class DeleteMessageCmd extends BaseQueueCmd<Integer> {

	private String name;
	private String id;

	public DeleteMessageCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * The Queue name.
	 */
	public DeleteMessageCmd qname(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Message id to delete.
	 */
	public DeleteMessageCmd id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * 1 if successful, 0 if the message was not found.
	 */
	@Override
	protected Integer exec(Jedis jedis) {
		Validator.create()
			.assertValidQname(name)
			.assertValidId(id);

		String key = config.redisNs() + name;

		Transaction tx = jedis.multi();

		tx.zrem(key, id);
		tx.hdel(key + Q, id, id + ":rc", id + ":fr");

		List result = tx.exec();

		if (toInt(result, 0) == 1 && toInt(result, 1) > 0) {
			return 1;
		}

		return 0;
	}
}