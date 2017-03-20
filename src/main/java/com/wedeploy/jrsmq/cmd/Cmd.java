package com.wedeploy.jrsmq.cmd;

public interface Cmd<T> {

	/**
	 * Executes a command and returns execution value.
	 */
	public T exec();

}