package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

import java.util.function.Supplier;

/**
 * Change the visibility timer of a single message. The time when the message
 * will be visible again is calculated from the current time (now) + vt.
 */
public class ChangeMessageVisibilityCmd extends BaseQueueCmd<Integer> {

	private final String changeMessageVisibilitySha1;
	private String qname;
	private String id;
	private int vt;

	public ChangeMessageVisibilityCmd(RedisSMQConfig config, Supplier<Jedis> jedisSupplier, String changeMessageVisibilitySha1) {
		super(config, jedisSupplier);
		this.changeMessageVisibilitySha1 = changeMessageVisibilitySha1;
	}

	/**
	 * The Queue name.
	 */
	public ChangeMessageVisibilityCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	/**
	 * The message id.
	 */
	public ChangeMessageVisibilityCmd id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * The length of time, in seconds, that this message will not be visible. Allowed values: 0-9999999.
	 */
	public ChangeMessageVisibilityCmd vt(int vt) {
		this.vt = vt;
		return this;
	}

	/**
	 * @return 1 if successful, 0 if the message was not found.
	 */
	@Override
	protected Integer exec(Jedis jedis) {
		Validator.create()
			.assertValidQname(qname)
			.assertValidVt(vt)
			.assertValidId(id);

		QueueDef q = getQueue(jedis, qname, false);

		Long foo = (Long) jedis.evalsha(changeMessageVisibilitySha1, 3, config.redisNs() + qname, id, String.valueOf(q.ts() + vt * 1000));

		return foo.intValue();
	}
}
