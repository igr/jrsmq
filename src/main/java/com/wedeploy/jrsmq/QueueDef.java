package com.wedeploy.jrsmq;

/**
 * Queue definition.
 */
public class QueueDef {
	private final long vt;
	private final int delay;
	private final int maxsize;
	private final long ts;
	private final String uid;
	private final String qname;

	public QueueDef(String qname, String vt, String delay, String maxsize, long ts, String uid) {
		this.qname = qname;
		this.vt = Long.valueOf(vt);
		this.delay = Integer.valueOf(delay);
		this.maxsize = Integer.valueOf(maxsize);
		this.ts = ts;
		this.uid = uid;
	}

	public String qname() {
		return qname;
	}

	public long vt() {
		return vt;
	}

	public int delay() {
		return delay;
	}

	public int maxsize() {
		return maxsize;
	}

	public long ts() {
		return ts;
	}

	public String uid() {
		return uid;
	}
}
