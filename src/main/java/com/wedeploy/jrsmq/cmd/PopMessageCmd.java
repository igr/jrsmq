package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.QueueMessage;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.function.Supplier;

/**
 * Receive the next message from the queue and delete it.
 * <br>
 * Important: This method deletes the message it receives right away.
 * There is no way to receive the message again if something goes wrong while working on the message.
 */
public class PopMessageCmd extends BaseQueueCmd<QueueMessage> {

	private final String popMessageSha1;
	private String qname;

	public PopMessageCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier, String popMessageSha1) {
		super(config, jedisSupplier);
		this.popMessageSha1 = popMessageSha1;
	}

	/**
	 * The Queue name.
	 */
	public PopMessageCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * @return {@link QueueMessage} or {@code null} if no message is there.
	 */
	@Override
	protected QueueMessage exec(Jedis jedis) {
		Validator.create().assertValidQname(qname);

		QueueDef q = getQueue(jedis, qname, false);

		List result = (List)jedis.evalsha(popMessageSha1, 2, config.redisNs() + qname, String.valueOf(q.ts()));

		return createQueueMessage(result);
	}
}
