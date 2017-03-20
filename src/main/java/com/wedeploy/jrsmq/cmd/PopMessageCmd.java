package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.QueueMessage;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

import java.util.List;

public class PopMessageCmd extends BaseQueueCmd<QueueMessage> {

	private final String popMessageSha1;
	private String qname;

	public PopMessageCmd(RedisSMQConfig config, Jedis jedis, String popMessageSha1) {
		super(config, jedis);
		this.popMessageSha1 = popMessageSha1;
	}

	public PopMessageCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	@Override
	public QueueMessage execute() {
		Validator.create().assertValidQname(qname);

		QueueDef q = getQueue(qname, false);

		List result = (List) jedis.evalsha(popMessageSha1, 2, config.redisNs() + qname, String.valueOf(q.ts()));

		return createQueueMessage(result);
	}
}
