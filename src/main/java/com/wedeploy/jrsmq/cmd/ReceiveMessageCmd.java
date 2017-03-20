package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.QueueMessage;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

import java.util.List;

public class ReceiveMessageCmd extends BaseQueueCmd<QueueMessage> {

	private final String receiveMessageSha1;
	private String name;
	private int vt = -1;

	public ReceiveMessageCmd(RedisSMQConfig config, Jedis jedis, String receiveMessageSha1) {
		super(config, jedis);
		this.receiveMessageSha1 = receiveMessageSha1;
	}

	public ReceiveMessageCmd qname(String name) {
		this.name = name;
		return this;
	}

	public ReceiveMessageCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	@Override
	public QueueMessage execute() {
		Validator.create()
			.assertValidQname(name);

		QueueDef q = getQueue(name, false);

		int vt = this.vt;
		if (vt == -1) {
			vt = q.vt();
		}
		Validator.create().assertValidVt(vt);

		@SuppressWarnings("unchecked")
		List result = (List) jedis.evalsha(
			receiveMessageSha1, 3, config.redisNs() + name, String.valueOf(q.ts()), String.valueOf(q.ts() + vt * 1000));

		return createQueueMessage(result);
	}
}
