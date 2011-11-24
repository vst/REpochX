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
package org.epochx.representation;

/**
 * An instance of <code>CandidateProgram</code> represents an individual
 * candidate solution to a problem. Specific subclasses represent the programs
 * in different ways.
 */
public abstract class CandidateProgram implements Cloneable, Comparable<CandidateProgram> {

	/**
	 * Calculates a quality score for this program. The exact calculation
	 * implementation varies by subclass. Fitnesses are standardised
	 * so implementations should return lower values for better programs. There
	 * is however no obligation for fitness values to be positive.
	 * 
	 * @return a standardised fitness score indicating the quality of this
	 *         candidate program.
	 */
	public abstract double getFitness();

	/**
	 * Calculates and returns the adjusted fitness of this program. A program's
	 * adjusted fitness lies between 0 and 1, with a larger value for better
	 * individuals. The fitness value returned from the <code>getFitness</code>
	 * method is used as the standardised fitness in the calculation, so it is
	 * assumed that the lowest possible value returned from that method is 0.0.
	 * 
	 * <p>
	 * The adjusted fitness is calculated using the formula:
	 * 
	 * <code><blockquote>
	 * adjusted-fitness = 1 / (1 + standardised-fitness)
	 * </blockquote></code>
	 * 
	 * @return
	 */
	public double getAdjustedFitness() {
		final double standardised = getFitness();
		final double adjusted = 1.0 / (1.0 + standardised);

		return adjusted;
	}

	/**
	 * Tests whether this <code>CandidateProgram</code> is valid according to
	 * any restrictions in the model. For example, certain implementations may
	 * test that it does not exceed any maximum depth or length parameter.
	 * 
	 * @return true if this program abides by all restrictions and false
	 *         otherwise.
	 */
	public abstract boolean isValid();

	/**
	 * Creates a copy of this candidate program. Subclass implementations should
	 * override this method to copy their internal program structure. It should
	 * always be true that the returned program is equal to this program
	 * according to the implementation of the <code>equals</code> method.
	 * 
	 * @return a new instance of <code>CandidateProgram</code> that is
	 *         equivalent to this <code>CandidateProgram</code>.
	 */
	@Override
	public CandidateProgram clone() {
		CandidateProgram clone = null;
		try {
			clone = (CandidateProgram) super.clone();
		} catch (final CloneNotSupportedException e) {
			assert false;
		}

		return clone;
	}

	/**
	 * Compares this program to another based upon fitness. Returns a negative
	 * integer if this program has a worse (larger) fitness value, zero if they
	 * have equal fitnesses and a positive integer if this program has a
	 * better (smaller) fitness value.
	 * 
	 * @param o the <code>CandidateProgram</code> instance to compare against.
	 * @return a negative integer, zero, or a positive integer if this program
	 *         has a worse, equal or better fitness than <code>o</code>
	 *         respectively.
	 */
	@Override
	public int compareTo(final CandidateProgram o) {
		if (o == null) {
			throw new NullPointerException("cannot compare to null");
		}

		final double thisFitness = getFitness();
		final double objFitness = o.getFitness();

		if (thisFitness > objFitness) {
			return -1;
		} else if (thisFitness == objFitness) {
			return 0;
		} else {
			return 1;
		}
	}
}
