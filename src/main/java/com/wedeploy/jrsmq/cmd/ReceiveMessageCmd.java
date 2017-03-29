package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.QueueMessage;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.function.Supplier;

import static com.wedeploy.jrsmq.Values.UNSET_VALUE;

/**
 * Receive the next message from the queue.
 */
public class ReceiveMessageCmd extends BaseQueueCmd<QueueMessage> {

	private final String receiveMessageSha1;
	private String name;
	private int vt = UNSET_VALUE;

	public ReceiveMessageCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier, String receiveMessageSha1) {
		super(config, jedisSupplier);
		this.receiveMessageSha1 = receiveMessageSha1;
	}

	/**
	 * The Queue name.
	 */
	public ReceiveMessageCmd qname(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Optional (Default: queue settings) length of time, in seconds, that the
	 * received message will be invisible to others. Allowed values: 0-9999999 (around 115 days)
	 */
	public ReceiveMessageCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	/**
	 * @return {@link QueueMessage} or {@code null} if message is not there.
	 */
	@Override
	protected QueueMessage exec(Jedis jedis) {
		Validator.create()
			.assertValidQname(name);

		QueueDef q = getQueue(jedis, name, false);

		int vt = this.vt;
		if (vt == UNSET_VALUE) {
			vt = q.vt();
		}
		Validator.create().assertValidVt(vt);

		@SuppressWarnings("unchecked")
		List result = (List) jedis.evalsha(
			receiveMessageSha1, 3, config.redisNs() + name, String.valueOf(q.ts()), String.valueOf(q.ts() + vt * 1000));

		return createQueueMessage(result);
	}
}
