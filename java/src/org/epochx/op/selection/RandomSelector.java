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
package org.epochx.op.selection;

import java.util.*;

import org.epochx.core.Model;
import org.epochx.op.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.tools.random.RandomNumberGenerator;

/**
 * A random selector is a program and pool selector which provides no selection
 * pressure. No reference is made to the programs' fitness scores.
 * 
 * <p>
 * If a model is provided then the following parameters are loaded upon every
 * configure event:
 * 
 * <ul>
 * <li>random number generator</li>
 * </ul>
 * 
 * <p>
 * If the <code>getModel</code> method returns <code>null</code> then no model
 * is set and whatever static parameters have been set as parameters to the
 * constructor or using the standard accessor methods will be used. If any
 * compulsory parameters remain unset when the selector is requested to select
 * programs, then an <code>IllegalStateException</code> will be thrown.
 * 
 * @see LinearRankSelector
 * @see FitnessProportionateSelector
 * @see TournamentSelector
 */
public class RandomSelector extends ConfigOperator<Model> implements ProgramSelector, PoolSelector {

	// Random number generator.
	private RandomNumberGenerator rng;

	// The current pool from which programs should be chosen.
	private List<CandidateProgram> pool;

	/**
	 * Constructs an instance of <code>RandomSelector</code> with the only
	 * necessary parameter given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 */
	public RandomSelector(final RandomNumberGenerator rng) {
		this((Model) null);

		this.rng = rng;
	}

	/**
	 * Constructs an instance of <code>RandomSelector</code>.
	 * 
	 * @param model the Model which defines the run parameters such as the
	 *        random number generator to use.
	 */
	public RandomSelector(final Model model) {
		super(model);
	}

	/**
	 * Configures the operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	/**
	 * Sets the population from which individual programs will be randomly
	 * selected.
	 * 
	 * @param pool the population of candidate programs from which programs
	 *        should be selected.
	 */
	@Override
	public void setSelectionPool(final List<CandidateProgram> pool) {
		this.pool = pool;
	}

	/**
	 * Randomly chooses and returns a program from the population with no bias.
	 * 
	 * @return a randomly selected program.
	 */
	@Override
	public CandidateProgram getProgram() {
		if ((pool == null) || pool.isEmpty()) {
			throw new IllegalStateException("selection pool cannot be "
					+ "null and must contain 1 or more CandidatePrograms");
		} else if (rng == null) {
			throw new IllegalStateException("random number generator not set");
		}

		return pool.get(rng.nextInt(pool.size()));
	}

	/**
	 * Randomly chooses programs from the given population up to a total of
	 * <code>poolSize</code> and returns them as a list. The generated pool may
	 * contain duplicate programs, and as such the pool size is allowed to be
	 * greater than the population size.
	 * 
	 * @param pop the population of CandidatePrograms from which the programs
	 *        in the pool should be chosen. Must not be null, nor empty.
	 * @param poolSize the number of programs that should be selected from the
	 *        population to form the pool. Must be 1 or greater.
	 * @return the randomly selected pool of candidate programs.
	 */
	@Override
	public List<CandidateProgram> getPool(final List<CandidateProgram> pop, final int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize must be greater than 0");
		} else if ((pop == null) || (pop.isEmpty())) {
			throw new IllegalArgumentException("population to select pool from must not be null nor empty");
		} else if (rng == null) {
			throw new IllegalStateException("random number generator not set");
		}

		// Construct our pool.
		final List<CandidateProgram> pool = new ArrayList<CandidateProgram>(poolSize);
		for (int i = 0; i < poolSize; i++) {
			pool.add(pop.get(rng.nextInt(pop.size())));
		}

		return pool;
	}

	/**
	 * Returns the random number generator that this selector is using or
	 * <code>null</code> if none has been set.
	 * 
	 * @return the rng the currently set random number generator.
	 */
	public RandomNumberGenerator getRNG() {
		return rng;
	}

	/**
	 * Sets the random number generator to use. If a model has been set then
	 * this parameter will be overwritten with the random number generator from
	 * that model on the next configure event.
	 * 
	 * @param rng the random number generator to set.
	 */
	public void setRNG(final RandomNumberGenerator rng) {
		this.rng = rng;
	}
}
