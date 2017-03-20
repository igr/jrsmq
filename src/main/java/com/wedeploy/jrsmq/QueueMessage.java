package com.wedeploy.jrsmq;

public class QueueMessage {
	private final String id;
	private final String message;
	private final long rc;
	private final long fr;
	private final long sent;

	public QueueMessage(String id, String message, long rc, long fr, long sent) {
		this.id = id;
		this.message = message;
		this.rc = rc;
		this.fr = fr;
		this.sent = sent;
	}

	/**
	 * The internal message id.
	 */
	public String id() {
		return id;
	}

	/**
	 * The message's contents.
	 */
	public String message() {
		return message;
	}

	/**
	 * Number of times this message was received.
	 */
	public long rc() {
		return rc;
	}

	/**
	 * Timestamp of when this message was first received.
	 */
	public long fr() {
		return fr;
	}

	/**
	 * Timestamp of when this message was sent / created.
	 */
	public long sent() {
		return sent;
	}
}
