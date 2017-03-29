package com.wedeploy.jrsmq;

import redis.clients.jedis.Protocol;

/**
 * RedsSMQ Configuration builder.
 */
public class RedisSMQConfig {

	private String host;
	private int port;
	private int timeout;
	private int database = Protocol.DEFAULT_DATABASE;
	private String ns;
	private String password;
	private String redisns;

	public RedisSMQConfig() {
		host("localhost");
		port(6379);
		timeout(5000);
		ns("rsmq");
	}

	/**
	 * Creates default configuration.
	 */
	public static RedisSMQConfig createDefaultConfig() {
		return new RedisSMQConfig();
	}

	public int database() {
		return database;
	}

	/**
	 * Sets redis server default database.
	 */
	public RedisSMQConfig database(int database) {
		this.database = database;
		return this;
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

	public String password() {
		return password;
	}

	/**
	 * Sets redis server password.
	 */
	public RedisSMQConfig password(String password) {
		this.password = password;
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

	public int timeout() {
		return timeout;
	}

	/**
	 * Sets redis server timeout.
	 */
	public RedisSMQConfig timeout(int timeout) {
		this.timeout = timeout;
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