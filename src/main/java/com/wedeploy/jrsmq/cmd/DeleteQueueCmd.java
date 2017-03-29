package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.Values;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.QUEUES;
import static com.wedeploy.jrsmq.Util.toInt;

/**
 * Delete a queue and all messages.
 */
public class DeleteQueueCmd extends BaseQueueCmd<Integer> {

	private String qname;

	public DeleteQueueCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * The Queue name.
	 */
	public DeleteQueueCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * @return 1
	 */
	@Override
	protected Integer exec(Jedis jedis) {
		Validator.create()
			.assertValidQname(qname);

		String key = config.redisNs() + qname;

		Transaction tx = jedis.multi();

		tx.del(key + Values.Q);
		tx.del(key);
		tx.srem(config.redisNs() + QUEUES, qname);

		List result = tx.exec();

		if (toInt(result, 0) == 0) {
			throw new RedisSMQException("Queue not found: " + qname);
		}

		return 1;
	}


}
