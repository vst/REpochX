/*
 * Copyright 2007-2011 Tom Castle & Lawrence Beadle
 * Licensed under GNU Lesser General Public License
 * 
 * This file is part of EpochX: genetic programming software for research
 * 
 * EpochX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EpochX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with EpochX. If not, see <http://www.gnu.org/licenses/>.
 * 
 * The latest version is available from: http://www.epochx.org
 */
package org.epochx.stats;

import java.util.Arrays;

/**
 * This class contains static utility methods for the generation or use of
 * statistics.
 */
public final class StatsUtils {

	/*
	 * Suppress default constructor for noninstantiability.
	 */
	private StatsUtils() {
		assert false;
	}

	/**
	 * Calculates and returns the mean average of an array of
	 * <code>double</code>. The provided argument must not be null nor an empty
	 * array.
	 * 
	 * @param values an array of doubles to calculate the mean average of.
	 * @return the mean average of the provided array of doubles.
	 */
	public static double ave(final double ... values) {
		if ((values != null) && (values.length != 0)) {
			double sum = 0;
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
			}
			return sum / values.length;
		} else {
			throw new IllegalArgumentException("cannot calculate average of " + "null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the mean average of an array of <code>int</code>.
	 * The provided argument must not be null nor an empty array.
	 * 
	 * @param values an array of int to calculate the mean average of.
	 * @return the mean average of the provided array of int.
	 */
	public static double ave(final int ... values) {
		if ((values != null) && (values.length != 0)) {
			int sum = 0;
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
			}
			return ((double) sum) / values.length;
		} else {
			throw new IllegalArgumentException("cannot calculate average of null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the standard deviation of an array of
	 * <code>double</code>. The provided argument must not be null nor an empty
	 * array.
	 * 
	 * @param values an array of doubles to calculate the standard deviation of.
	 * @return the standard deviation of the provided array of doubles.
	 */
	public static double stdev(final double ... values) {
		return stdev(values, ave(values));
	}

	/**
	 * Calculates and returns the standard deviation of an array of
	 * <code>double</code>. The provided <code>values</code>argument must not be
	 * null nor an empty array. The <code>ave</code> argument supplies the mean
	 * average of the values if it is known to save calculating it again.
	 * 
	 * @param values an array of doubles to calculate the standard deviation of.
	 * @param ave the mean average of the values.
	 * @return the standard deviation of the provided array of doubles.
	 */
	public static double stdev(final double[] values, final double ave) {
		if ((values != null) && (values.length != 0)) {
			// Sum the squared differences.
			double sqDiff = 0;
			for (int i = 0; i < values.length; i++) {
				sqDiff += Math.pow(values[i] - ave, 2);
			}

			// Take the square root of the average.
			return Math.sqrt(sqDiff / values.length);
		} else {
			throw new IllegalArgumentException("cannot calculate standard deviation of null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the standard deviation of an array of
	 * <code>int</code>. The provided argument must not be null nor an empty
	 * array.
	 * 
	 * @param values an array of int to calculate the standard deviation of.
	 * @return the standard deviation of the provided array of ints.
	 */
	public static double stdev(final int ... values) {
		return stdev(values, ave(values));
	}

	/**
	 * Calculates and returns the standard deviation of an array of
	 * <code>int</code>. The provided <code>values</code>argument must not be
	 * null nor an empty array. The <code>ave</code> argument supplies the mean
	 * average of the values if it is known to save calculating it again.
	 * 
	 * @param values an array of int to calculate the standard deviation of.
	 * @param ave the mean average of the values.
	 * @return the standard deviation of the provided array of ints.
	 */
	public static double stdev(final int[] values, final double ave) {
		if ((values != null) && (values.length != 0)) {
			// Sum the squared differences.
			double sqDiff = 0;
			for (int i = 0; i < values.length; i++) {
				sqDiff += Math.pow(values[i] - ave, 2);
			}

			// Take the square root of the average.
			return Math.sqrt(sqDiff / values.length);
		} else {
			throw new IllegalArgumentException("cannot calculate average of null or empty array of values");
		}
	}

	/**
	 * Finds the maximum value of an array of <code>double</code> and returns
	 * its index in the array. The provided argument must not be null nor an
	 * empty array.
	 * 
	 * @param values an array of double.
	 * @return the index of the maximum value in the provided array of doubles.
	 */
	public static int maxIndex(final double[] values) {
		if ((values != null) && (values.length != 0)) {
			double max = 0;
			int maxIndex = -1;
			for (int i = 0; i < values.length; i++) {
				if (values[i] > max) {
					max = values[i];
					maxIndex = i;
				}
			}
			return maxIndex;
		} else {
			throw new IllegalArgumentException("cannot calculate maximum index of null or empty array of values");
		}
	}

	/**
	 * Finds the maximum value of an array of <code>int</code> and returns
	 * its index in the array. The provided argument must not be null nor an
	 * empty array.
	 * 
	 * @param values an array of int.
	 * @return the index of the maximum value in the provided array of ints.
	 */
	public static int maxIndex(final int[] values) {
		if ((values != null) && (values.length != 0)) {
			int max = 0;
			int maxIndex = -1;
			for (int i = 0; i < values.length; i++) {
				if (values[i] > max) {
					max = values[i];
					maxIndex = i;
				}
			}
			return maxIndex;
		} else {
			throw new IllegalArgumentException("cannot calculate maximum index of null or empty array of values");
		}
	}

	/**
	 * Finds the minimum value of an array of <code>double</code> and returns
	 * its index in the array. The provided argument must not be null nor an
	 * empty array.
	 * 
	 * @param values an array of double.
	 * @return the index of the minimum value in the provided array of doubles.
	 */
	public static int minIndex(final double[] values) {
		if ((values != null) && (values.length != 0)) {
			double min = Double.MAX_VALUE;
			int minIndex = -1;
			for (int i = 0; i < values.length; i++) {
				if (values[i] < min) {
					min = values[i];
					minIndex = i;
				}
			}
			return minIndex;
		} else {
			throw new IllegalArgumentException("cannot calculate minimum index of null or empty array of values");
		}
	}

	/**
	 * Finds the minimum value of an array of <code>int</code> and returns
	 * its index in the array. The provided argument must not be null nor an
	 * empty array.
	 * 
	 * @param values an array of int.
	 * @return the index of the minimum value in the provided array of ints.
	 */
	public static int minIndex(final int[] values) {
		if ((values != null) && (values.length != 0)) {
			int min = Integer.MAX_VALUE;
			int minIndex = -1;
			for (int i = 0; i < values.length; i++) {
				if (values[i] < min) {
					min = values[i];
					minIndex = i;
				}
			}
			return minIndex;
		} else {
			throw new IllegalArgumentException("cannot calculate minimum index of null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the median of an array of <code>double</code>. The
	 * provided <code>values</code>argument must not be null nor an empty array.
	 * 
	 * @param values an array of doubles to calculate the median of.
	 * @return the median of the provided array of doubles.
	 */
	public static double median(final double ... values) {
		if ((values != null) && (values.length != 0)) {
			// Sort the array.
			Arrays.sort(values);

			// Pick out the middle value.
			final int medianIndex = (int) Math.floor(values.length / 2);
			double median = values[medianIndex - 1];

			// There might have been an even number - use average of 2 medians.
			if ((values.length % 2) == 0) {
				median += values[medianIndex];
				median = median / 2;
			}

			return median;
		} else {
			throw new IllegalArgumentException("cannot calculate median of null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the median of an array of <code>int</code>. The
	 * provided <code>values</code>argument must not be null nor an empty array.
	 * 
	 * @param values an array of int to calculate the median of.
	 * @return the median of the provided array of ints.
	 */
	public static double median(final int ... values) {
		if ((values != null) && (values.length != 0)) {
			// Sort the array.
			Arrays.sort(values);

			// Pick out the middle value.
			final int medianIndex = (int) Math.floor(values.length / 2);
			int median = values[medianIndex - 1];

			// There might have been an even number - use average of 2 medians.
			if ((values.length % 2) == 0) {
				median += values[medianIndex];
				median = median / 2;
			}

			return median;
		} else {
			throw new IllegalArgumentException("cannot calculate median of null or empty array of values");
		}
	}

	/**
	 * Calculates and returns the 95% confidence interval of an array of
	 * <code>double</code>. The returned value represents an interval so the
	 * actual interval is +/- the returned value either side of the mean.
	 * The provided <code>values</code>argument must not be null nor an empty
	 * array.
	 * 
	 * @param values an array of double to calculate the 95% confidence interval
	 *        for.
	 * @return the confidence interval either side of the mean for the provided
	 *         array of doubles.
	 */
	public static double ci95(final double ... values) {
		return ci95(values, stdev(values));
	}

	/**
	 * Calculates and returns the 95% confidence interval of an array of
	 * <code>double</code>. The returned value represents an interval so the
	 * actual interval is +/- the returned value either side of the mean.
	 * The provided <code>values</code>argument must not be null nor an empty
	 * array. The <code>stdev</code> argument supplies the standard deviation
	 * of the values if it is known to save calculating it again.
	 * 
	 * @param values an array of double to calculate the 95% confidence interval
	 *        for.
	 * @param stdev the standard deviation of the values.
	 * @return the confidence interval either side of the mean for the provided
	 *         array of doubles.
	 */
	public static double ci95(final double[] values, final double stdev) {
		final double ci = 1.96 * (stdev / Math.sqrt(values.length));

		return ci;
	}

	public static double ci95(final int ... values) {
		return ci95(values, stdev(values));
	}

	/**
	 * Calculates and returns the 95% confidence interval of an array of
	 * <code>int</code>. The returned value represents an interval so the
	 * actual interval is +/- the returned value either side of the mean.
	 * The provided <code>values</code>argument must not be null nor an empty
	 * array. The <code>stdev</code> argument supplies the standard deviation
	 * of the values if it is known to save calculating it again.
	 * 
	 * @param values an array of int to calculate the 95% confidence interval
	 *        for.
	 * @param stdev the standard deviation of the values.
	 * @return the confidence interval either side of the mean for the provided
	 *         array of ints.
	 */
	public static double ci95(final int[] values, final double stdev) {
		final double ci = 1.96 * (stdev / Math.sqrt(values.length));

		return ci;
	}
}
