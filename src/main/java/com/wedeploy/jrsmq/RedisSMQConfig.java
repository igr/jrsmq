package com.wedeploy.jrsmq;

/**
 * RedsSMQ Configuration builder.
 */
public class RedisSMQConfig {

	private String host;
	private int port;
	private String ns;
	private String redisns;

	public RedisSMQConfig() {
		setHost("localhost");
		setPort(6379);
		setNs("rsmq");
	}

	/**
	 * Creates default configuration.
	 */
	public static RedisSMQConfig createDefaultConfig() {
		return new RedisSMQConfig();
	}

	public String getHost() {
		return host;
	}

	/**
	 * Sets redis server host address.
	 */
	public RedisSMQConfig setHost(String host) {
		this.host = host;
		return this;
	}

	public int getPort() {
		return port;
	}

	/**
	 * Sets redis server port.
	 */
	public RedisSMQConfig setPort(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Returns name space.
	 */
	public String getNs() {
		return ns;
	}

	/**
	 * Returns redis name space.
	 */
	public String getRedisNs() {
		return redisns;
	}

	/**
	 * Sets name space value used for {@link #getRedisNs()}.
	 */
	public RedisSMQConfig setNs(String ns) {
		this.ns = ns;
		this.redisns = ns + ':';
		return this;
	}
}