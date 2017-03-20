package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueAttributes;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

import static com.wedeploy.jrsmq.Names.Q;

public class SetQueueAttributesCmd extends BaseQueueCmd<QueueAttributes> {

	private String qname;
	private long vt = -1;
	private int maxSize = -1;
	private int delay = -1;
	private final GetQueueAttributesCmd getQueueAttributes;

	public SetQueueAttributesCmd(RedisSMQConfig config, Jedis jedis) {
		super(config, jedis);
		this.getQueueAttributes = new GetQueueAttributesCmd(config, jedis);
	}

	public SetQueueAttributesCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	public SetQueueAttributesCmd vt(long vt) {
		this.vt = vt;
		return this;
	}

	public SetQueueAttributesCmd maxsize(int maxSize) {
		this.maxSize = maxSize;
		return this;
	}

	public SetQueueAttributesCmd delay(int delay) {
		this.delay = delay;
		return this;
	}


	@Override
	public QueueAttributes execute() {
		Validator.create().assertValidQname(qname);

		getQueue(qname, false); // just to check if it is an existing queue

		String key = config.redisNs() + qname + Q;

		List<String> times = jedis.time();

		Transaction tx = jedis.multi();

		tx.hset(key, "modified", times.get(0));

		Validator validator = Validator.create();
		if (vt != -1) {
			validator.assertValidVt(vt);
			tx.hset(key, "vt", String.valueOf(vt));
		}
		if (maxSize != -1) {
			validator.assertValidMaxSize(maxSize);
			tx.hset(key, "maxsize", String.valueOf(maxSize));
		}
		if (delay != -1) {
			validator.assertValidDelay(delay);
			tx.hset(key, "delay", String.valueOf(delay));
		}

		tx.exec();

		return getQueueAttributes.qname(qname).execute();
	}
}