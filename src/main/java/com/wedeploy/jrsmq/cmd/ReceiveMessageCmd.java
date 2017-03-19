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

	public ReceiveMessageCmd fromQueue(String name) {
		this.name = name;
		return this;
	}

	public ReceiveMessageCmd withVt(int vt) {
		this.vt = vt;
		return this;
	}

	@Override
	public QueueMessage execute() {
		Validator.create()
			.assertValidQname(name);

		QueueDef q = getQueue(name, false);

		long vt = this.vt;
		if (vt == -1) {
			vt = q.vt();
		}
		Validator.create().assertValidVt(vt);

		@SuppressWarnings("unchecked")
		List result = (List<String>) jedis.evalsha(
			receiveMessageSha1, 3, config.getRedisNs() + name, String.valueOf(q.ts()), String.valueOf(q.ts() + vt * 1000));

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
