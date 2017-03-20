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
		host("localhost");
		port(6379);
		ns("rsmq");
	}

	/**
	 * Creates default configuration.
	 */
	public static RedisSMQConfig createDefaultConfig() {
		return new RedisSMQConfig();
	}

	public String host() {
		return host;
	}

	/**
	 * Sets redis server host address.
	 */
	public RedisSMQConfig host(String host) {
		this.host = host;
		return this;
	}

	public int port() {
		return port;
	}

	/**
	 * Sets redis server port.
	 */
	public RedisSMQConfig port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Returns name space.
	 */
	public String ns() {
		return ns;
	}

	/**
	 * Returns redis name space.
	 */
	public String redisNs() {
		return redisns;
	}

	/**
	 * Sets name space value used for {@link #redisNs()}.
	 */
	public RedisSMQConfig ns(String ns) {
		this.ns = ns;
		this.redisns = ns + ':';
		return this;
	}
}