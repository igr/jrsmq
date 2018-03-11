package com.oblac.jrsmq;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilTest {

	@Test
	public void testFormatZeroPad() {
		assertEquals("123", Util.formatZeroPad("123", 1));
		assertEquals("123", Util.formatZeroPad("123", 2));
		assertEquals("123", Util.formatZeroPad("123", 3));
		assertEquals("0123", Util.formatZeroPad("123", 4));
		assertEquals("00123", Util.formatZeroPad("123", 5));
		assertEquals("000123", Util.formatZeroPad("123", 6));
	}

	@Test
	public void testMakeId() {
		assertEquals(3, Util.makeId(3).length());
	}

}
