package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.QueueMessage;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.RedisSMQException;
import com.wedeploy.jrsmq.Util;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

import static com.wedeploy.jrsmq.Names.Q;

public abstract class BaseQueueCmd<T> implements Cmd<T> {

	protected final RedisSMQConfig config;
	protected final Jedis jedis;

	public BaseQueueCmd(RedisSMQConfig config, Jedis jedis) {
		this.config = config;
		this.jedis = jedis;
	}

	/**
	 * Reads a queue from the Redis.
	 */
	protected QueueDef getQueue(String qname, boolean generateUid) {
		Transaction tx = jedis.multi();

		String key = config.redisNs() + qname + Q;

		tx.hmget(key, "vt", "delay", "maxsize");
		tx.time();

		List<Object> results = tx.exec();

		@SuppressWarnings("unchecked")
		List<String> respGet = (List<String>) results.get(0);

		if (respGet.get(0) == null || respGet.get(1) == null || respGet.get(2) == null) {
			throw new RedisSMQException("Queue not found: " + qname);
		}

		@SuppressWarnings("unchecked")
		List<String> respTime = (List<String>) results.get(1);

		String ms = Util.formatZeroPad(respTime.get(1), 6);
		long ts = Long.valueOf(respTime.get(0) + ms.substring(0, 3));

		String id = null;
		if (generateUid) {
			id = Util.makeId(22);
			id = Long.toString(Long.valueOf(respTime.get(0) + ms), 36) + id;
		}

		return new QueueDef(qname, respGet.get(0), respGet.get(1), respGet.get(2), ts, id);
	}

	/**
	 * Creates a queue message from resulting list.
	 */
	protected QueueMessage createQueueMessage(List result) {
		if (result.isEmpty()) {
			return null;
		}

		return new QueueMessage(
			(String) result.get(0),
			(String) result.get(1),
			(Long) result.get(2),
			Long.parseLong((String)result.get(3)),
			Long.valueOf(((String)result.get(0)).substring(0, 10), 36) / 1000
		);
	}
}