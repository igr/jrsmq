package com.oblac.jrsmq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.oblac.jrsmq.Values.UNSET_VALUE;

public class ValidatorTest {

	@Test
	public void testValidMaxSize() {
		Validator.create()
			.assertValidMaxSize(1024)
			.assertValidMaxSize(1025);
	}

	@Test
	public void testValidMaxSize_lessThen() {
		Assertions.assertThrows(Exception.class, () -> Validator.create().assertValidMaxSize(1023));
	}

	@Test
	public void testValidSize_greaterThen() {
		Assertions.assertThrows(Exception.class, () -> Validator.create().assertValidMaxSize(65537));
	}

	@Test
	public void testValidName() {
		Validator.create().assertValidQname("123");
	}

	@Test
	public void testValidName_longer() {
		Assertions.assertThrows(Exception.class, () -> {
			StringBuilder sb = new StringBuilder(161);
			for (int i = 0; i < 161; i++) {
				sb.append('j');
			}
			Validator.create().assertValidQname(sb.toString());
		});
	}

	@Test
	public void testValidName_empty() {
		Assertions.assertThrows(Exception.class, () -> Validator.create().assertValidQname(""));
	}

	@Test
	public void testAtLeastOneSet() {
		Validator.create().assertAtLeastOneSet(1, UNSET_VALUE, UNSET_VALUE);
	}

	@Test
	public void testAtLeastOneSet_allUnset() {
		Assertions.assertThrows(Exception.class, () ->
			Validator.create().assertAtLeastOneSet(UNSET_VALUE, UNSET_VALUE, UNSET_VALUE));
	}
}
