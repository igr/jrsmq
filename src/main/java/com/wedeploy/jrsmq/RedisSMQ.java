package com.wedeploy.jrsmq;

import com.wedeploy.jrsmq.cmd.CreateQueueCmd;
import com.wedeploy.jrsmq.cmd.DeleteMessageCmd;
import com.wedeploy.jrsmq.cmd.DeleteQueueCmd;
import com.wedeploy.jrsmq.cmd.GetQueueAttributesCmd;
import com.wedeploy.jrsmq.cmd.ListQueuesCmd;
import com.wedeploy.jrsmq.cmd.ReceiveMessageCmd;
import com.wedeploy.jrsmq.cmd.SendMessageCmd;
import com.wedeploy.jrsmq.cmd.SetQueueAttributesCmd;
import redis.clients.jedis.Jedis;

public class RedisSMQ {

	protected final Jedis jedis;
	protected final RedisSMQConfig config;

	public RedisSMQ() {
		this(RedisSMQConfig.createDefaultConfig());
	}

	public RedisSMQ(RedisSMQConfig config) {
		this.config = config;
		this.jedis = new Jedis(config.host(), config.port());
	}

	// ---------------------------------------------------------------- connect

	private boolean connected;

	/**
	 * Connects to Redis.
	 */
	public RedisSMQ connect() {
		this.jedis.connect();
		initScript();
		this.connected = true;
		return this;
	}

	/**
	 * Returns {@code true} if client is connected to Redis.
	 */
	public boolean isConnected() {
		return connected;
	}

	// ---------------------------------------------------------------- cmds

	/**
	 * Creates a queue.
	 * @see CreateQueueCmd
	 */
	public CreateQueueCmd createQueue() {
		return new CreateQueueCmd(config, jedis);
	}

	/**
	 * Deletes a queue.
	 * @see DeleteQueueCmd
	 */
	public DeleteQueueCmd deleteQueue() {
		return new DeleteQueueCmd(config, jedis);
	}

	/**
	 * Deletes a message.
	 * @see DeleteMessageCmd
	 */
	public DeleteMessageCmd deleteMessage() {
		return new DeleteMessageCmd(config, jedis);
	}

	/**
	 * Returns queue attributes.
	 * @see GetQueueAttributesCmd
	 */
	public GetQueueAttributesCmd getQueueAttributes() {
		return new GetQueueAttributesCmd(config, jedis);
	}

	public SetQueueAttributesCmd setQueueAttributes() {
		return new SetQueueAttributesCmd(config, jedis);
	}

	/**
	 * Lists queues.
	 * @see ListQueuesCmd
	 */
	public ListQueuesCmd listQueues() {
		return new ListQueuesCmd(config, jedis);
	}

	/**
	 * Receives a message.
	 * @see ReceiveMessageCmd
	 */
	public ReceiveMessageCmd receiveMessage() {
		return new ReceiveMessageCmd(config, jedis, receiveMessageSha1);
	}

	/**
	 * Sends a message to a queue.
	 * @see SendMessageCmd
	 */
	public SendMessageCmd sendMessage() {
		return new SendMessageCmd(config, jedis);
	}

	/**
	 * Disconnects from Redis.
	 */
	public void quit() {
		try {
			this.jedis.disconnect();
		}
		catch (Exception ex) {
			// ignore
		}
		this.connected = false;
	}

	// ---------------------------------------------------------------- scripts

	private static final String SCRIPT_POPMESSAGE = "local msg = redis.call(\"ZRANGEBYSCORE\", KEYS[1], \"-inf\", KEYS[2], \"LIMIT\", \"0\", \"1\") if #msg == 0 then return {} end redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", \"totalrecv\", 1) local mbody = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1]) local rc = redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", msg[1] .. \":rc\", 1) local o = {msg[1], mbody, rc} if rc==1 then table.insert(o, KEYS[2]) else local fr = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\") table.insert(o, fr) end redis.call(\"ZREM\", KEYS[1], msg[1]) redis.call(\"HDEL\", KEYS[1] .. \":Q\", msg[1], msg[1] .. \":rc\", msg[1] .. \":fr\") return o";
	private static final String SCRIPT_RECEIVEMESSAGE = "local msg = redis.call(\"ZRANGEBYSCORE\", KEYS[1], \"-inf\", KEYS[2], \"LIMIT\", \"0\", \"1\") if #msg == 0 then return {} end redis.call(\"ZADD\", KEYS[1], KEYS[3], msg[1]) redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", \"totalrecv\", 1) local mbody = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1]) local rc = redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", msg[1] .. \":rc\", 1) local o = {msg[1], mbody, rc} if rc==1 then redis.call(\"HSET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\", KEYS[2]) table.insert(o, KEYS[2]) else local fr = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\") table.insert(o, fr) end return o";
	private static final String SCRIPT_CHANGEMESSAGEVISIBILITY = "local msg = redis.call(\"ZSCORE\", KEYS[1], KEYS[2]) if not msg then return 0 end redis.call(\"ZADD\", KEYS[1], KEYS[3], KEYS[2]) return 1";

	protected String popMessageSha1;
	protected String receiveMessageSha1;
	protected String changeMessageVisibility;

	protected void initScript() {
		popMessageSha1 = jedis.scriptLoad(SCRIPT_POPMESSAGE);
		receiveMessageSha1 = jedis.scriptLoad(SCRIPT_RECEIVEMESSAGE);
		changeMessageVisibility = jedis.scriptLoad(SCRIPT_CHANGEMESSAGEVISIBILITY);
	}

}
