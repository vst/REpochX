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
 * Linear rank selection chooses programs by fitness rank. All the programs in
 * the population are ranked according to their fitness from lowest to highest.
 * Each program is then assigned a probability according to their rank in a
 * linear fashion with a gradient as given at construction. Programs are
 * selected according to this probability.
 * 
 * <p>
 * Valid gradients are values between 0.0 and 1.0. A gradient of 1.0 is
 * generally not very useful since it will result in all ranks being assigned
 * the same probability, and as such will provide no selection pressure at all.
 * A gradient of 0.0 on the other hand will provide the largest selection
 * pressure, with the least fit individual having a probability of selection of
 * 0.0 meaning it will never be selected.
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
 * @see FitnessProportionateSelector
 * @see RandomSelector
 * @see TournamentSelector
 */
public class LinearRankSelector extends ConfigOperator<Model> implements ProgramSelector, PoolSelector {

	// Internal program selectors used by the 2 different tasks.
	private final ProgramLinearRankSelector programSelection;
	private final ProgramLinearRankSelector poolSelection;

	// Random number generator.
	private RandomNumberGenerator rng;

	// The gradient of the probabilities.
	private double gradient;

	// Probability of selecting the most fit program.
	private double nPlus;

	// Probability of selecting the least fit program.
	private double nMinus;

	/**
	 * Constructs an instance of <code>LinearRankSelector</code> with the only
	 * necessary parameter given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 */
	public LinearRankSelector(final RandomNumberGenerator rng) {
		this((Model) null);

		this.rng = rng;
	}

	/**
	 * Constructs an instance of <code>LinearRankSelector</code> with all the
	 * necessary parameters given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 * @param gradient a value between 0.0 and 1.0 which indicates the gradient
	 *        at which fitnesses are assigned to ranks.
	 */
	public LinearRankSelector(final RandomNumberGenerator rng, final double gradient) {
		this((Model) null, gradient);

		this.rng = rng;
	}

	/**
	 * Constructs an instance of <code>LinearRankSelector</code> with a default
	 * gradient value of <code>0.2</code>.
	 * 
	 * @param model the Model which defines the run parameters such as the
	 *        random number generator to use.
	 */
	public LinearRankSelector(final Model model) {
		this(model, 0.2);
	}

	/**
	 * Constructs an instance of <code>LinearRankSelector</code>.
	 * 
	 * @param model the Model which defines the run parameters such as the
	 *        random number generator to use.
	 * @param gradient a value between 0.0 and 1.0 which indicates the gradient
	 *        at which fitnesses are assigned to ranks.
	 */
	public LinearRankSelector(final Model model, final double gradient) {
		super(model);
		setGradient(gradient);

		// Construct the internal program selectors.
		programSelection = new ProgramLinearRankSelector();
		poolSelection = new ProgramLinearRankSelector();
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	/**
	 * Sets the gradient of the linear probabilities. Valid gradients are
	 * between <code>0.0</code> and <code>1.0</code> inclusive, where a smaller
	 * value provides a steeper gradient and a larger probability difference
	 * between the worst and best individuals. A gradient of <code>1.0</code>
	 * will be a level gradient and as such will provide no selection pressure
	 * at all.
	 * 
	 * @param gradient the gradient to use when assigning probabilities, between
	 *        0.0 and 1.0.
	 */
	public void setGradient(final double gradient) {
		if ((gradient < 0.0) || (gradient > 1.0)) {
			throw new IllegalArgumentException("linear rank gradient " + "must be between 0.0 and 1.0");
		}

		this.gradient = gradient;

		nMinus = 2 / (gradient + 1);
		nPlus = (2 * gradient) / (gradient + 1);

		assert ((nMinus + nPlus) == 2);
	}

	/**
	 * Gets the gradient of the linear probabilities.
	 * 
	 * @return a double between 0.0 and 1.0 representing the gradient of the
	 *         probabilities where 0.0 is a steep gradient and 1.0 is no
	 *         gradient.
	 */
	public double getGradient() {
		return gradient;
	}

	/**
	 * Gets the probability of the worst program being selected with the
	 * current gradient.
	 * 
	 * @return the probability of the worst program being selected.
	 */
	public double getWorstProbability(final int popSize) {
		return nPlus / popSize;
	}

	/**
	 * Gets the probability of the best program being selected with the
	 * current gradient.
	 * 
	 * @return the probability of the best program being selected.
	 */
	public double getBestProbability(final int popSize) {
		return nMinus / popSize;
	}

	/**
	 * Sets the population from which programs will be selected. The
	 * probabilities are calculated once at this point based upon the linear
	 * fitness rank.
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
	 * the probabilities which were assigned based on fitness rank.
	 * 
	 * @return a program selected from the current population based on fitness
	 *         rank.
	 */
	@Override
	public CandidateProgram getProgram() {
		return programSelection.getProgram();
	}

	/**
	 * Constructs a pool of programs from the population, choosing each one
	 * with the program selection element of LinearRankSelector. The size of
	 * the pool created will be equal to the poolSize argument. The generated
	 * pool may contain duplicate programs, and as such the pool size may be
	 * greater than the population size.
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

	/*
	 * This is a little strange, but we use an inner class here so we can
	 * create 2 separate instances of it internally for the 2 tasks of pool
	 * selection and program selection which is necessary because they both
	 * select from different pools. The original implementation of getPool
	 * created an internal instance of LinearRankSelector but it is not
	 * advisable to create components between model configurations.
	 */
	private class ProgramLinearRankSelector implements ProgramSelector {

		// The current population from which programs should be chosen.
		private List<CandidateProgram> pool;

		// An array of size pool.size() giving probabilities for each program.
		private double probabilities[];

		@Override
		public void setSelectionPool(final List<CandidateProgram> pool) {
			if ((pool == null) || pool.isEmpty()) {
				throw new IllegalArgumentException("selection pool cannot be "
						+ "null and must contain 1 or more CandidatePrograms");
			}

			// Sort the pool of programs from worst to best.
			this.pool = pool;
			Collections.sort(pool, Collections.reverseOrder());

			// Create array of probabilities.
			final int popSize = pool.size();
			probabilities = new double[popSize];
			double total = 0;

			// Calculate probabilities this way so last element can safely
			// receive whatever is left.
			for (int i = 0; i < popSize; i++) {
				final int n = popSize - i;
				final double p = (1.0 / popSize) * (nMinus + ((nPlus - nMinus) * ((n - 1.0) / (popSize - 1.0))));

				total += p;
				probabilities[i] = total;
			}

			// Ensure the final probability is at least 1.0, we may have lost
			// some precision.
			probabilities[popSize - 1] = 1.0;
		}

		@Override
		public CandidateProgram getProgram() {
			if (rng == null) {
				throw new IllegalStateException("random number generator not set");
			}

			final double ran = rng.nextDouble();

			assert ((ran >= 0.0) && (ran <= 1.0));

			for (int i = 0; i < probabilities.length; i++) {
				if (ran <= probabilities[i]) {
					return pool.get(i);
				}
			}

			// This shouldn't ever happen assuming the probabilities add up to
			// 1.
			assert false;

			return null;
		}
	}
}
