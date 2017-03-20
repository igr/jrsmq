package com.wedeploy.jrsmq;

import java.util.regex.Pattern;

/**
 * Convenient validator for arguments.
 */
public class Validator {

	public static Validator create() {
		return new Validator();
	}

	public Validator assertValidDelay(int delay) {
		return assertValidVt(delay);
	}

	public Validator assertValidVt(int vt) {
		if (vt < 0 || vt > 9999999) {
			throw validationException(vt, "is out of range [0, 9999999].");
		}

		return this;
	}

	public Validator assertValidMaxSize(int maxsize) {
		if (maxsize == -1) {
			return this;
		}
		if (maxsize < 1024 || maxsize > 65536) {
			throw validationException(maxsize, "is out of range [1024, 65536] and not -1.");
		}

		return this;
	}

	private final Pattern namePattern = Pattern.compile("^([a-zA-Z0-9_-]){1,160}$");

	public Validator assertValidQname(String qname) {
		if (qname == null) {
			throw validationException(qname, "for name is missing.");
		}
		if (!namePattern.matcher(qname).matches()) {
			throw validationException(qname, "is invalid name.");
		}
		return this;
	}

	private final Pattern idPattern = Pattern.compile("^([a-zA-Z0-9:]){32}$");

	public Validator assertValidId(String id) {
		if (id == null) {
			throw validationException("for id is missing.");
		}
		if (!idPattern.matcher(id).matches()) {
			throw validationException(id, "is invalid id.");
		}
		return this;
	}

	public void assertValidMessage(QueueDef q, String message) {
		if (message == null) {
			throw validationException("for message is missing.");
		}
		if (q.maxsize() != -1 && message.length() > q.maxsize()) {
			throw validationException("for message is too long.");
		}
	}

	// ---------------------------------------------------------------- exceptions

	/**
	 * Creates validation exception and formats the message.
	 */
	private RedisSMQException validationException(String cause) {
		return new RedisSMQException("Value " + cause);
	}
	/**
	 * Creates validation exception and formats the message.
	 */
	private RedisSMQException validationException(Object value, String cause) {
		return new RedisSMQException("Value " + value + " " + cause);
	}

}
