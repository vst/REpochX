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

/**
 * An implementation of this interface is used to generate a stream of numbers
 * with pseudo random qualities, in different data types.
 */
public interface RandomNumberGenerator {

	/**
	 * Get the next <code>int</code> between <code>0</code> (inclusive) and
	 * <code>n</code> (exclusive).
	 * 
	 * @param n the upper limit of the generation.
	 * @return the next <code>int</code> in the pseudo random sequence.
	 */
	public int nextInt(int n);

	/**
	 * Get the next <code>int</code> with the only limits being the bounds of
	 * Java's <code>int</code> data type.
	 * 
	 * @return the next <code>int</code> in the pseudo random sequence.
	 */
	public int nextInt();

	/**
	 * Get the next <code>double</code> in the range <code>0.0</code>
	 * (inclusive) and <code>1.0</code> (exclusive).
	 * 
	 * @return the next <code>double</code> in the pseudo random sequence.
	 */
	public double nextDouble();

	/**
	 * Get the next <code>boolean</code> value.
	 * 
	 * @return the next <code>true</code> or <code>false</code> value in the
	 *         pseudo random sequence.
	 */
	public boolean nextBoolean();

	/**
	 * Set the initial seed of the random number generation. Two instances of
	 * the same implementation of <code>RandomNumberGenerator</code> given the
	 * same seed, should produce the same sequence of numbers.
	 * 
	 * @param seed the initial seed.
	 */
	public void setSeed(long seed);

}
