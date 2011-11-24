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
 * Tournament selection chooses programs through a tournament performed on a
 * subset of the population. In tournament selection, x programs are randomly
 * selected from the population to enter a 'tournament'. The program with the
 * best fitness in the tournament then becomes the selected program. The
 * tournament size, x, is given as an argument to the constructor.
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
 * @see LinearRankSelector
 */
public class TournamentSelector extends ConfigOperator<Model> implements ProgramSelector, PoolSelector {

	// Internal program selectors used by the 2 different tasks.
	private final ProgramTournamentSelector poolSelection;
	private final ProgramTournamentSelector programSelection;

	// The size of the tournment from which the best program will be taken.
	private int tournamentSize;

	// Random number generator.
	private RandomNumberGenerator rng;

	/**
	 * Constructs an instance of <code>LinearRankSelector</code> with all the
	 * necessary parameters given.
	 * 
	 * @param rng a <code>RandomNumberGenerator</code> used to lead
	 *        non-deterministic behaviour.
	 */
	public TournamentSelector(final RandomNumberGenerator rng, final int tournamentSize) {
		this((Model) null, tournamentSize);

		this.rng = rng;
	}

	/**
	 * Construct a tournament selector with the specified tournament size.
	 * 
	 * @param tournamentSize the number of programs in each tournament.
	 */
	public TournamentSelector(final Model model, final int tournamentSize) {
		super(model);

		this.tournamentSize = tournamentSize;

		// Construct the internal program selectors.
		poolSelection = new ProgramTournamentSelector();
		programSelection = new ProgramTournamentSelector();
	}

	/**
	 * Configures this operator with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		rng = getModel().getRNG();
	}

	/**
	 * Sets the population from which programs will be selected to participate
	 * in tournaments.
	 * 
	 * @param pop the population of candidate programs from which programs
	 *        should be selected.
	 */
	@Override
	public void setSelectionPool(final List<CandidateProgram> pop) {
		programSelection.setSelectionPool(pop);
	}

	/**
	 * Randomly creates a tournament, then selects the candidate program with
	 * the best fitness from that tournament. The size of the tournament is
	 * given at instantiation.
	 * 
	 * @return the best program from a randomly generated tournament.
	 */
	@Override
	public CandidateProgram getProgram() {
		return programSelection.getProgram();
	}

	/**
	 * Returns the number of programs that compete in each tournament selection.
	 * 
	 * @return the number of programs that a tournament consists of.
	 */
	public int getTournamentSize() {
		return tournamentSize;
	}

	/**
	 * Sets the number of programs that should compete in each tournament.
	 * 
	 * @param tournamentSize the number of programs to use in each tournament.
	 */
	public void setTournamentSize(final int tournamentSize) {
		this.tournamentSize = tournamentSize;
	}

	/**
	 * Constructs a pool of programs from the population, choosing each one
	 * with the program selection element of TournamentSelector. The size of
	 * the pool created will be equal to the poolSize argument. The generated
	 * pool may contain duplicate programs, and as such the pool size may be
	 * greater than the population size.
	 * 
	 * @param pop the population of CandidatePrograms from which the programs
	 *        in the pool should be chosen. Must not be null, nor empty.
	 * @param poolSize the number of programs that should be selected from the
	 *        population to form the pool. Must be 1 or greater.
	 * @return the pool of candidate programs selected using tournament
	 *         selection.
	 */
	@Override
	public List<CandidateProgram> getPool(final List<CandidateProgram> pop, final int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("poolSize must be greater than 0");
		} else if ((pop == null) || (pop.isEmpty())) {
			throw new IllegalArgumentException("population to select pool from must not be null nor empty");
		}

		// Construct the pool using the internal program selector.
		final List<CandidateProgram> pool = new ArrayList<CandidateProgram>(poolSize);
		poolSelection.setSelectionPool(pop);
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

		// Set internal selector's rng.
		poolSelection.randomSelector.setRNG(rng);
		programSelection.randomSelector.setRNG(rng);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModel(final Model model) {
		super.setModel(model);

		// Set internal selector's models.
		poolSelection.randomSelector.setModel(model);
		programSelection.randomSelector.setModel(model);
	}

	/*
	 * This is a little strange, but we use an inner class here so we can
	 * create 2 separate instances of it internally for the 2 tasks of pool
	 * selection and program selection which is necessary because they both
	 * select from different pools. The original implementation of getPool
	 * created an internal instance of TournamentSelector but it is not
	 * advisable to create components between model configurations.
	 */
	private class ProgramTournamentSelector implements ProgramSelector {

		// We use a random selector to construct tournaments.
		private final RandomSelector randomSelector;

		public ProgramTournamentSelector() {
			final Model model = getModel();
			if (model == null) {
				randomSelector = new RandomSelector(rng);
			} else {
				randomSelector = new RandomSelector(model);
			}
		}

		@Override
		public void setSelectionPool(final List<CandidateProgram> pop) {
			// We'll be using a random selector to construct a tournament.
			randomSelector.setSelectionPool(pop);
		}

		@Override
		public CandidateProgram getProgram() {
			CandidateProgram bestProgram = null;
			double bestFitness = Double.POSITIVE_INFINITY;

			// Choose and compare randomly selected programs.
			for (int i = 0; i < tournamentSize; i++) {
				final CandidateProgram p = randomSelector.getProgram();
				final double fitness = p.getFitness();
				if (fitness < bestFitness) {
					bestFitness = fitness;
					bestProgram = p;
				}
			}

			return bestProgram;
		}
	}
}
