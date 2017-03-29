package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueAttributes;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Util;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.Q;

/**
 * Get queue attributes, counter and stats.
 */
public class GetQueueAttributesCmd extends BaseQueueCmd<QueueAttributes> {

	private String qname;

	public GetQueueAttributesCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier) {
		super(config, jedisSupplier);
	}

	/**
	 * The Queue name.
	 */
	public GetQueueAttributesCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * @return {@link QueueAttributes}
	 */
	@Override
	protected QueueAttributes exec(Jedis jedis) {
		Validator.create().assertValidQname(qname);

		List<String> times = jedis.time();

		String key = config.redisNs() + qname;

		Transaction tx = jedis.multi();

		tx.hmget(key + Q, "vt", "delay", "maxsize", "totalrecv", "totalsent", "created", "modified");
		tx.zcard(key);
		tx.zcount(key, times.get(0) + "000", "+inf");

		List results = tx.exec();

		if (results.get(0) == null) {
			throw new RedisSMQException("Queue not found: " + qname);
		}

		@SuppressWarnings("unchecked")
		List<String> rec0 = (List<String>) results.get(0);

		QueueAttributes qa = new QueueAttributes(
			Integer.parseInt(rec0.get(0)),
			Integer.parseInt(rec0.get(1)),
			Integer.parseInt(rec0.get(2)),
			Util.safeParseLong(rec0.get(3)),
			Util.safeParseLong(rec0.get(4)),
			Long.parseLong(rec0.get(5)),
			Long.parseLong(rec0.get(6)),
			(Long) results.get(1),
			(Long) results.get(2)
		);

		return qa;
	}
}
