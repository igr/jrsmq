package com.wedeploy.jrsmq;

import com.wedeploy.jrsmq.cmd.ChangeMessageVisibilityCmd;
import com.wedeploy.jrsmq.cmd.CreateQueueCmd;
import com.wedeploy.jrsmq.cmd.DeleteMessageCmd;
import com.wedeploy.jrsmq.cmd.DeleteQueueCmd;
import com.wedeploy.jrsmq.cmd.GetQueueAttributesCmd;
import com.wedeploy.jrsmq.cmd.ListQueuesCmd;
import com.wedeploy.jrsmq.cmd.PopMessageCmd;
import com.wedeploy.jrsmq.cmd.ReceiveMessageCmd;
import com.wedeploy.jrsmq.cmd.SendMessageCmd;
import com.wedeploy.jrsmq.cmd.SetQueueAttributesCmd;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.function.Supplier;

public class RedisSMQ {

	protected final JedisPool jedisPool;
	protected final RedisSMQConfig config;

	public RedisSMQ() {
		this(RedisSMQConfig.createDefaultConfig());
	}

	public RedisSMQ(RedisSMQConfig config) {
		this.config = config;
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxIdle(128);
		poolConfig.setMaxTotal(128);
		jedisPool = new JedisPool(
			poolConfig, config.host(), config.port(), config.timeout(), config.password(), config.database(), null);
		initScript(jedisPool.getResource());
	}

	// ---------------------------------------------------------------- connect

	/**
	 * Gets a connection from the pool.
	 */
	protected Jedis jedis() {
		return jedisPool.getResource();
	}

	/**
	 * Jedis supplier.
	 */
	protected Supplier<Jedis> jedisSupplier = this::jedis;

	// ---------------------------------------------------------------- cmds

	/**
	 * Changes the visibility timer of a single message.
	 * @see ChangeMessageVisibilityCmd
	 */
	public ChangeMessageVisibilityCmd changeMessageVisibility() {
		return new ChangeMessageVisibilityCmd(config, jedisSupplier, changeMessageVisibility);
	}
	/**
	 * Creates a new queue.
	 * @see CreateQueueCmd
	 */
	public CreateQueueCmd createQueue() {
		return new CreateQueueCmd(config, jedisSupplier);
	}

	/**
	 * Deletes a queue and all messages.
	 * @see DeleteQueueCmd
	 */
	public DeleteQueueCmd deleteQueue() {
		return new DeleteQueueCmd(config, jedisSupplier);
	}

	/**
	 * Deletes a message.
	 * @see DeleteMessageCmd
	 */
	public DeleteMessageCmd deleteMessage() {
		return new DeleteMessageCmd(config, jedisSupplier);
	}

	/**
	 * Returns queue attributes, counter and stats.
	 * @see GetQueueAttributesCmd
	 */
	public GetQueueAttributesCmd getQueueAttributes() {
		return new GetQueueAttributesCmd(config, jedisSupplier);
	}

	/**
	 * Sets queue parameters.
	 * @see SetQueueAttributesCmd
	 */
	public SetQueueAttributesCmd setQueueAttributes() {
		return new SetQueueAttributesCmd(config, jedisSupplier);
	}

	/**
	 * Lists all queues.
	 * @see ListQueuesCmd
	 */
	public ListQueuesCmd listQueues() {
		return new ListQueuesCmd(config, jedisSupplier);
	}

	/**
	 * Receives the next message from the queue and <b>deletes</b> it.
	 * @see PopMessageCmd
	 */
	public PopMessageCmd popMessage() {
		return new PopMessageCmd(config, jedisSupplier, popMessageSha1);
	}

	/**
	 * Receives the next message from the queue.
	 * @see ReceiveMessageCmd
	 */
	public ReceiveMessageCmd receiveMessage() {
		return new ReceiveMessageCmd(config, jedisSupplier, receiveMessageSha1);
	}

	/**
	 * Sends a new message.
	 * @see SendMessageCmd
	 */
	public SendMessageCmd sendMessage() {
		return new SendMessageCmd(config, jedisSupplier);
	}

	/**
	 * Disconnects the redis client.
	 */
	public void quit() {
		try {
			this.jedisPool.close();
			this.jedisPool.destroy();
		}
		catch (Exception ex) {
			// ignore
		}
	}

	// ---------------------------------------------------------------- scripts

	private static final String SCRIPT_POPMESSAGE = "local msg = redis.call(\"ZRANGEBYSCORE\", KEYS[1], \"-inf\", KEYS[2], \"LIMIT\", \"0\", \"1\") if #msg == 0 then return {} end redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", \"totalrecv\", 1) local mbody = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1]) local rc = redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", msg[1] .. \":rc\", 1) local o = {msg[1], mbody, rc} if rc==1 then table.insert(o, KEYS[2]) else local fr = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\") table.insert(o, fr) end redis.call(\"ZREM\", KEYS[1], msg[1]) redis.call(\"HDEL\", KEYS[1] .. \":Q\", msg[1], msg[1] .. \":rc\", msg[1] .. \":fr\") return o";
	private static final String SCRIPT_RECEIVEMESSAGE = "local msg = redis.call(\"ZRANGEBYSCORE\", KEYS[1], \"-inf\", KEYS[2], \"LIMIT\", \"0\", \"1\") if #msg == 0 then return {} end redis.call(\"ZADD\", KEYS[1], KEYS[3], msg[1]) redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", \"totalrecv\", 1) local mbody = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1]) local rc = redis.call(\"HINCRBY\", KEYS[1] .. \":Q\", msg[1] .. \":rc\", 1) local o = {msg[1], mbody, rc} if rc==1 then redis.call(\"HSET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\", KEYS[2]) table.insert(o, KEYS[2]) else local fr = redis.call(\"HGET\", KEYS[1] .. \":Q\", msg[1] .. \":fr\") table.insert(o, fr) end return o";
	private static final String SCRIPT_CHANGEMESSAGEVISIBILITY = "local msg = redis.call(\"ZSCORE\", KEYS[1], KEYS[2]) if not msg then return 0 end redis.call(\"ZADD\", KEYS[1], KEYS[3], KEYS[2]) return 1";

	protected String popMessageSha1;
	protected String receiveMessageSha1;
	protected String changeMessageVisibility;

	protected void initScript(Jedis jedis) {
		popMessageSha1 = jedis.scriptLoad(SCRIPT_POPMESSAGE);
		receiveMessageSha1 = jedis.scriptLoad(SCRIPT_RECEIVEMESSAGE);
		changeMessageVisibility = jedis.scriptLoad(SCRIPT_CHANGEMESSAGEVISIBILITY);
	}

}
