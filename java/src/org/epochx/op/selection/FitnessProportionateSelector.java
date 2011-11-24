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
 * Fitness proportionate selection chooses programs with a probability
 * proportional to their adjusted fitness.
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
 * @see RandomSelector
 * @see TournamentSelector
 */
public class FitnessProportionateSelector extends ConfigOperator<Model> implements ProgramSelector, PoolSelector {

	// Internal program selectors used by the 2 different tasks.
	private final ProgramFitnessProportionateSelector programSelection;
	private final ProgramFitnessProportionateSelector poolSelection;

	// Random number generator.
	private RandomNumberGenerator rng;

	// Whether over-selection should be used or not.
	private boolean overSelection;

	/**
	 * Constructs an instance of <code>FitnessProportionateSelector</code> with
	 * the only compulsory parameter given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 */
	public FitnessProportionateSelector(final RandomNumberGenerator rng) {
		this((Model) null, false);
	}

	/**
	 * Constructs an instance of <code>FitnessProportionateSelector</code> with
	 * the only necessary parameters given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 * @param overSelection true if greedy over-selection should be used and
	 *        false otherwise.
	 */
	public FitnessProportionateSelector(final RandomNumberGenerator rng, final boolean overSelection) {
		this((Model) null, overSelection);
	}

	/**
	 * Constructs an instance of <code>FitnessProportionateSelector</code> that
	 * does not use over-selection.
	 * 
	 * @param model the Model which defines the run parameters such as the
	 *        random number generator to use.
	 */
	public FitnessProportionateSelector(final Model model) {
		this(model, false);
	}

	/**
	 * Constructs an instance of <code>FitnessProportionateSelector</code> that
	 * does not use over-selection.
	 * 
	 * @param model the Model which defines the run parameters such as the
	 *        random number generator to use.
	 * @param overSelection true if greedy over-selection should be used and
	 *        false otherwise.
	 */
	public FitnessProportionateSelector(final Model model, final boolean overSelection) {
		super(model);

		this.overSelection = overSelection;

		// Construct the internal program selectors.
		programSelection = new ProgramFitnessProportionateSelector();
		poolSelection = new ProgramFitnessProportionateSelector();
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	/**
	 * Sets the population from which programs will be selected.
	 * 
	 * @param pool the population of candidate programs from which programs
	 *        should be selected.
	 */
	@Override
	public void setSelectionPool(final List<CandidateProgram> pool) {
		programSelection.setSelectionPool(pool);
	}

	/**
	 * Selects a candidate program at random from the population according to
	 * the probabilities which were assigned proportional to the inverse of the
	 * program's fitness.
	 * 
	 * @return a program selected from the current population based on fitness.
	 */
	@Override
	public CandidateProgram getProgram() {
		return programSelection.getProgram();
	}

	/**
	 * Constructs a pool of programs from the population, choosing each one
	 * with the program selection element of FitnessProportionateSelector. The
	 * size of the pool created will be equal to the poolSize argument. The
	 * generated pool may contain duplicate programs, and as such the pool size
	 * may be greater than the population size.
	 * 
	 * @param pop the population of CandidatePrograms from which the programs
	 *        in the pool should be chosen.
	 * @param poolSize the number of programs that should be selected from the
	 *        population to form the pool. The poolSize must be 1 or
	 *        greater.
	 * @return the pool of candidate programs selected according to fitness
	 *         rank.
	 */
	@Override
	public List<CandidateProgram> getPool(final List<CandidateProgram> pop, final int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize must be greater than 0");
		} else if ((pop == null) || (pop.isEmpty())) {
			throw new IllegalArgumentException("population to select pool from must not be null nor empty");
		}

		// Use internal program selector to select poolSize programs.
		poolSelection.setSelectionPool(pop);
		final List<CandidateProgram> pool = new ArrayList<CandidateProgram>();

		for (int i = 0; i < poolSize; i++) {
			pool.add(poolSelection.getProgram());
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

	/**
	 * Returns true if over-selection is enabled and false otherwise.
	 * 
	 * @return true if over-selection is being used, false otherwise.
	 */
	public boolean isOverSelectionEnabled() {
		return overSelection;
	}

	/**
	 * Sets whether over-selection should be used.
	 * 
	 * @param overSelection true if over-selection should be used and false
	 *        otherwise.
	 */
	public void setOverSelectionEnabled(final boolean overSelection) {
		this.overSelection = overSelection;

		// Any existing selection pool will need to be set again so it can be
		// sorted.
		if (programSelection.pool != null) {
			setSelectionPool(programSelection.pool);
		}
	}

	/**
	 * Cumulative fitness of programs to go into over-selected group.
	 * 
	 * @return
	 */
	protected double getOverSelectionProportion(final int popSize) {
		double proportion = 0.32;

		// This should change based upon the population size.
		int delimiter = 1000;
		while (popSize > delimiter) {
			proportion /= 2;
			delimiter *= 2;
		}

		return proportion;
	}

	/*
	 * This is a little strange, but we use an inner class here so we can
	 * create 2 separate instances of it internally for the 2 tasks of pool
	 * selection and program selection which is necessary because they both
	 * select from different pools. The original implementation of getPool
	 * created an internal instance of FitnessProportionateSelector but it is
	 * not advisable to create components between model configurations.
	 */
	private class ProgramFitnessProportionateSelector implements ProgramSelector {

		// The current population from which programs should be chosen.
		private List<CandidateProgram> pool;

		// Normalised normalised.
		private double[] normalised;

		// Cumulative fitness of programs to go into over-selected group.
		private double overSelectionProportion;

		@Override
		public void setSelectionPool(final List<CandidateProgram> pool) {
			if ((pool == null) || pool.isEmpty()) {
				throw new IllegalArgumentException("selection pool cannot be "
						+ "null and must contain 1 or more CandidatePrograms");
			}
			this.pool = pool;

			// If using over-selection then ensure the pool is sorted first.
			if (overSelection) {
				Collections.sort(pool, Collections.reverseOrder());
			}
			overSelectionProportion = getOverSelectionProportion(pool.size());

			// Get adjusted fitnesses for each program.
			final double[] adjusted = new double[pool.size()];
			double adjustedSum = 0.0;
			for (int i = 0; i < adjusted.length; i++) {
				final double adjustedFitness = pool.get(i).getAdjustedFitness();
				adjusted[i] = adjustedFitness;
				adjustedSum += adjustedFitness;
			}

			// Calculate cumulative normalised fitnesses.
			normalised = new double[pool.size()];
			double normalisedSum = 0.0;
			for (int i = 0; i < normalised.length; i++) {
				normalisedSum += (adjusted[i] / adjustedSum);
				normalised[i] = normalisedSum;
			}

			// Ensure the final probability is 1.0.
			normalised[normalised.length - 1] = 1.0;
		}

		@Override
		public CandidateProgram getProgram() {
			if ((pool == null) || pool.isEmpty()) {
				throw new IllegalStateException("selection pool cannot be "
						+ "null and must contain 1 or more CandidatePrograms");
			} else if (rng == null) {
				throw new IllegalStateException("random number generator not set");
			}

			double ran = rng.nextDouble();

			if (overSelection) {
				// Scale the random number to choose from correct part of array.
				if (rng.nextDouble() < 0.8) {
					// Select from over-selected group.
					ran *= overSelectionProportion;
				} else {
					// Select from under-selected group.
					ran = (ran * (1.0 - overSelectionProportion)) + overSelectionProportion;
				}
			}

			assert ((ran >= 0.0) && (ran <= 1.0));

			for (int i = 0; i < normalised.length; i++) {
				if (ran <= normalised[i]) {
					return pool.get(i);
				}
			}

			// This shouldn't ever happen assuming the final probability is 1.0.
			assert false;

			return null;
		}
	}

}
