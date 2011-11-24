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
package org.epochx.tools.random;

import java.util.Random;

/**
 * Provides a mechanism for using Java's random number generation through
 * EpochX's supported <code>RandomNumberGenerator</code> interface.
 * 
 * @see Random
 */
public class JavaRandom implements RandomNumberGenerator {

	// The underlying Java random number generator.
	private final Random rand;

	/**
	 * Constructs a <code>JavaRandom</code> random number generator.
	 */
	public JavaRandom() {
		rand = new Random();
	}

	/**
	 * Constructs a <code>JavaRandom</code> random number generator with the
	 * specified seed.
	 * 
	 * @param seed the initial seed.
	 */
	public JavaRandom(final long seed) {
		rand = new Random(seed);
	}

	/**
	 * Constructs a <code>JavaRandom</code> continuing the sequence from the
	 * given <code>Random</code> instance's pseudorandom number generation.
	 * 
	 * @param rand the <code>Random</code> instance to use for random number
	 *        generation.
	 */
	public JavaRandom(final Random rand) {
		this.rand = rand;
	}

	/**
	 * Returns the next randomly generated <code>boolean</code> value, as
	 * returned by the underlying Java pseudorandom number generation.
	 * 
	 * @return true or false, as randomly selected by the Java random number
	 *         generator.
	 * @see Random
	 */
	@Override
	public boolean nextBoolean() {
		return rand.nextBoolean();
	}

	/**
	 * Returns the next randomly generated <code>double</code> value, as
	 * returned by the underlying Java pseudorandom number generation.
	 * 
	 * @return a randomly selected double value in the range <code>0.0</code>
	 *         (inclusive) to <code>1.0</code> (exclusive) as selected by the
	 *         Java
	 *         random number generator.
	 * @see Random
	 */
	@Override
	public double nextDouble() {
		return rand.nextDouble();
	}

	/**
	 * Returns the next randomly generated <code>int</code> value between
	 * <code>0</code>(inclusive) and <code>n</code> (exclusive), as returned by
	 * the underlying Java pseudorandom number generation.
	 * 
	 * @param n the upper limit of the generation.
	 * @return a randomly selected <code>int</code> value in the range
	 *         <code>0</code> (inclusive) to <code>n</code> (exclusive) as
	 *         selected by
	 *         the Java random number generator.
	 * @see Random
	 */
	@Override
	public int nextInt(final int n) {
		return rand.nextInt(n);
	}

	/**
	 * Returns the next randomly generated <code>int</code> value, as returned
	 * by the underlying Java pseudorandom number generation. All 2<sup>32</sup>
	 * possible <code>int</code> values may be returned.
	 * 
	 * @return a randomly selected <code>int</code> value as selected by the
	 *         Java random number generator.
	 * @see Random
	 */
	@Override
	public int nextInt() {
		return rand.nextInt();
	}

	/**
	 * Sets the seed of the underlying Java random number generator.
	 * 
	 * @param seed the initial seed.
	 */
	@Override
	public void setSeed(final long seed) {
		rand.setSeed(seed);
	}
}
