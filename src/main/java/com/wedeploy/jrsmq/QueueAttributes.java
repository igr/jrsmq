package com.wedeploy.jrsmq;

public class QueueAttributes {

	private final int vt;
	private final int delay;
	private final int maxSize;
	private final long totalRecv;
	private final long totalSent;
	private final long created;
	private final long modified;
	private final long msgs;
	private final long hiddenMsgs;

	public QueueAttributes(int vt, int delay, int maxSize, long totalRecv, long totalSent, long created, long modified, long msgs, long hiddenMsgs) {
		this.vt = vt;
		this.delay = delay;
		this.maxSize = maxSize;
		this.totalRecv = totalRecv;
		this.totalSent = totalSent;
		this.created = created;
		this.modified = modified;
		this.msgs = msgs;
		this.hiddenMsgs = hiddenMsgs;
	}

	/**
	 * The visibility timeout for the queue in seconds.
	 */
	public int vt() {
		return vt;
	}

	/**
	 * The delay for new messages in seconds.
	 */
	public int delay() {
		return delay;
	}

	/**
	 * The maximum size of a message in bytes.
	 */
	public int maxSize() {
		return maxSize;
	}

	/**
	 * Total number of messages received from the queue.
	 */
	public long totalRecv() {
		return totalRecv;
	}

	/**
	 * Total number of messages sent to the queue.
	 */
	public long totalSent() {
		return totalSent;
	}

	/**
	 * Timestamp (epoch in seconds) when the queue was created
	 */
	public long created() {
		return created;
	}

	/**
	 *  Timestamp (epoch in seconds) when the queue was last modified with setQueueAttributes().
	 */
	public long modified() {
		return modified;
	}

	/**
	 * Current number of messages in the queue.
	 */
	public long msgs() {
		return msgs;
	}

	/**
	 * Current number of hidden / not visible messages. A message can be hidden while "in flight" due to a vt parameter or when sent with a delay.
	 */
	public long hiddenMsgs() {
		return hiddenMsgs;
	}
}
