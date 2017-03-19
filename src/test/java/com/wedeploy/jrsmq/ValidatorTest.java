package com.wedeploy.jrsmq;

import org.junit.Test;

public class ValidatorTest {

	@Test
	public void testValidMaxSize() {
		Validator.create()
			.assertValidMaxSize(1024)
			.assertValidMaxSize(1025);
	}

	@Test(expected = Exception.class)
	public void testValidMaxSize_lessThen() {
		Validator.create().assertValidMaxSize(1023);
	}
	@Test(expected = Exception.class)
	public void testValidSize_greaterThen() {
		Validator.create().assertValidMaxSize(65537);
	}

	@Test
	public void testValidName() {
		Validator.create().assertValidQname("123");
	}

	@Test(expected = Exception.class)
	public void testValidName_longer() {
		StringBuilder sb = new StringBuilder(161);
		for (int i = 0; i < 161; i++) {
			sb.append('j');
		}
		Validator.create().assertValidQname(sb.toString());
	}
	@Test(expected = Exception.class)
	public void testValidName_empty() {
		Validator.create().assertValidQname("");
	}

}
