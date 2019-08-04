package pl.com.salsoft.exercise2.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalUtilsTest {
	@Test
	public void testFromPrice() {
		// Given
		final BigDecimal price = new BigDecimal("100.25");

		// When
		final long result = BigDecimalUtils.fromPrice(price);

		// Then
		assertEquals(10025, result);
	}

	@Test(expected = NullPointerException.class)
	public void testFromPriceError() {
		// Given
		// Nothing

		// When
		BigDecimalUtils.fromPrice(null);

		// Then
		// Exception is thrown
	}

	@Test
	public void testFromPriceExtraScaleHigh() {
		// Given
		final BigDecimal price = new BigDecimal("100.256");

		// When
		final long result = BigDecimalUtils.fromPrice(price);

		// Then
		assertEquals(10026, result);
	}

	@Test
	public void testFromPriceExtraScaleLow() {
		// Given
		final BigDecimal price = new BigDecimal("100.251");

		// When
		final long result = BigDecimalUtils.fromPrice(price);

		// Then
		assertEquals(10025, result);
	}

	@Test
	public void testToPrice() {
		// Given
		final long value = 12345;

		// When
		final BigDecimal result = BigDecimalUtils.toPrice(value);

		// Then
		assertEquals(new BigDecimal("123.45"), result);
	}

	@Test
	public void testToPriceLowValue() {
		// Given
		final long value = 3;

		// When
		final BigDecimal result = BigDecimalUtils.toPrice(value);

		// Then
		assertEquals(new BigDecimal("0.03"), result);
	}

	@Test
	public void testToPriceNegative() {
		// Given
		final long value = -12345;

		// When
		final BigDecimal result = BigDecimalUtils.toPrice(value);

		// Then
		assertEquals(new BigDecimal("-123.45"), result);
	}
}
