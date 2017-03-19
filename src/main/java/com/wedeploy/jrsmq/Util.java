package com.wedeploy.jrsmq;

import java.util.List;
import java.util.Random;

/**
 * Some utility functions.
 */
public class Util {

	/**
	 * Returns string representation number padded with zeros
	 */
	public static String formatZeroPad(String numString, int count) {
		if (count <= numString.length()) {
			return numString;
		}
		StringBuilder sb = new StringBuilder(count - numString.length());
		for (int i = numString.length(); i < count; i++) {
			sb.append('0');
		}
		return sb.append(numString).toString();
	}

	private static final String MAKEID_POSSIBLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final Random MAKEID_RND = new Random();

	/**
	 * Makes unique ID of given length;
	 */
	public static String makeId(int len) {
		StringBuilder id = new StringBuilder(len);

		for (int j = 0; j < len; j++) {
			int rndNdx = MAKEID_RND.nextInt(MAKEID_POSSIBLE.length());
			id.append(MAKEID_POSSIBLE.charAt(rndNdx));
		}
		return id.toString();
	}

	/**
	 * Converts list element to an integer.
	 * Assumes that list is of Numbers.
	 */
	public static int toInt(List results, int index) {
		try {
			return ((Number) results.get(index)).intValue();
		}
		catch (Exception ex) {
			throw new RedisSMQException("Invalid result value");
		}
	}

}
