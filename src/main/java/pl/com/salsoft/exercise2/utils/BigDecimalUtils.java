package pl.com.salsoft.exercise2.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.NonNull;

/**
 * A collection of couple utility methods used in the application for BigDecimal manipulations.
 */
public class BigDecimalUtils {
	private static final BigDecimal HUNDRED = new BigDecimal(100);

	/**
	 * Converts BigDecimal representation to long representation by multiplying the value by 100 and rounding to a whole figure.
	 * @param value BigDecimal value to convert.
	 * @return Long representation of the value as primitive long type.
	 */
	public static long fromPrice(@NonNull final BigDecimal value) {
		return value.multiply(HUNDRED).setScale(0, RoundingMode.HALF_UP).longValue();
	}

	/**
	 * Converts long representation to BigDecimal representation by dividing by 100.
	 * @param value Long value to convert.
	 * @return BigDecimal representation with scale of 2.
	 */
	public static BigDecimal toPrice(final long value) {
		return new BigDecimal(value).divide(BigDecimalUtils.HUNDRED, 2, RoundingMode.HALF_UP);
	}
}
