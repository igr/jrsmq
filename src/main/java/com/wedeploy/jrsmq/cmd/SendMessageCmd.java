package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import static com.wedeploy.jrsmq.Names.Q;
import static com.wedeploy.jrsmq.Names.TOTALSENT;

public class SendMessageCmd extends BaseQueueCmd<String> {

	private String qname;
	private String message;
	private int delay;

	public SendMessageCmd(RedisSMQConfig config, Jedis jedis) {
		super(config, jedis);
	}

	public SendMessageCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	public SendMessageCmd message(String message) {
		this.message = message;
		return this;
	}

	public SendMessageCmd delay(int delay) {
		this.delay = delay;
		return this;
	}

	@Override
	public String execute() {
		QueueDef q = getQueue(qname, true);

		Validator.create()
			.assertValidQname(qname)
			.assertValidDelay(delay)
			.assertValidMessage(q, message);

		Transaction tx = jedis.multi();

		String key = config.redisNs() + qname + Q;

		tx.zadd(config.redisNs() + qname, q.ts() + delay * 1000, q.uid());
		tx.hset(key, q.uid(), message);
		tx.hincrBy(key, TOTALSENT, 1);

		tx.exec();

		return q.uid();
	}
}
