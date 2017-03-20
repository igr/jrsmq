package com.wedeploy.jrsmq;

public class QueueAttributes {

	private final long vt;
	private final int delay;
	private final int maxSize;
	private final long totalRecv;
	private final long totalSent;
	private final long created;
	private final long modified;
	private final long msgs;
	private final long hiddenMsgs;

	public QueueAttributes(long vt, int delay, int maxSize, long totalRecv, long totalSent, long created, long modified, long msgs, long hiddenMsgs) {
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

	public long vt() {
		return vt;
	}

	public int delay() {
		return delay;
	}

	public int maxSize() {
		return maxSize;
	}

	public long totalRecv() {
		return totalRecv;
	}

	public long totalSent() {
		return totalSent;
	}

	public long created() {
		return created;
	}

	public long modified() {
		return modified;
	}

	public long msgs() {
		return msgs;
	}

	public long hiddenMsgs() {
		return hiddenMsgs;
	}
}
