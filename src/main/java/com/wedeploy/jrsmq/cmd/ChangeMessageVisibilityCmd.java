package com.wedeploy.jrsmq.cmd;

import com.wedeploy.jrsmq.QueueDef;
import com.wedeploy.jrsmq.RedisSMQConfig;
import com.wedeploy.jrsmq.Validator;
import redis.clients.jedis.Jedis;

public class ChangeMessageVisibilityCmd extends BaseQueueCmd<Integer> {

	private final String changeMessageVisibilitySha1;
	private String qname;
	private String id;
	private int vt;

	public ChangeMessageVisibilityCmd(RedisSMQConfig config, Jedis jedis, String changeMessageVisibilitySha1) {
		super(config, jedis);
		this.changeMessageVisibilitySha1 = changeMessageVisibilitySha1;
	}

	public ChangeMessageVisibilityCmd qname(String qname) {
		this.qname = qname;
		return this;
	}

	public ChangeMessageVisibilityCmd id(String id) {
		this.id = id;
		return this;
	}

	public ChangeMessageVisibilityCmd vt(int vt) {
		this.vt = vt;
		return this;
	}


	@Override
	public Integer execute() {
		Validator.create()
			.assertValidQname(qname)
			.assertValidVt(vt)
			.assertValidId(id);

		QueueDef q = getQueue(qname, false);

		Long foo = (Long) jedis.evalsha(changeMessageVisibilitySha1, 3, config.redisNs() + qname, id, String.valueOf(q.ts() + vt * 1000));

		return foo.intValue();
	}
}
