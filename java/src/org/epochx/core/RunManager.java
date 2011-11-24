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
package org.epochx.core;

import static org.epochx.stats.StatField.*;

import java.util.List;

import org.epochx.life.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.*;

/**
 * Instances of this class are responsible for executing single evolutionary
 * runs. Execution will be controlled by parameters retrieved from the
 * <code>Model</code> provided to the constructor.
 * 
 * <p>
 * Users of the EpochX framework would not typically create and use instances of
 * this class directly, but rather would through use of the <code>Model</code>'s
 * <code>run</code> class method which will execute an instance of this class
 * multiple times.
 * 
 * <p>
 * Instances of this class will update the {@link Stats} class with data about
 * the run as the run progresses. For the statistics available from the
 * <code>Stats</code>, view the {@link StatField} class and any extending
 * classes.
 * 
 * <p>
 * Use of the run manager will generate the following events:
 * 
 * <p>
 * <table border="1">
 * <tr>
 * <th>Event</th>
 * <th>Revert</th>
 * <th>Modify</th>
 * <th>Raised when?</th>
 * </tr>
 * <tr>
 * <td>onConfigure</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Immediately before the onRunStart event.</td>
 * </tr>
 * <tr>
 * <td>onRunStart</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Before the run starts.</td>
 * </tr>
 * <tr>
 * <td>onRunEnd</td>
 * <td>no</td>
 * <td>no</td>
 * <td>After the run has terminated.</td>
 * </tr>
 * </table>
 */
public class RunManager implements ConfigListener {

	// The controlling model.
	private final Model model;

	// Core components.
	private final GenerationManager generation;
	private final InitialisationManager initialisation;

	private int noGenerations;
	private double terminationFitness;

	// The best program found so far during the run.
	private CandidateProgram bestProgram;

	// The fitness of the best program found so far during the run.
	private double bestFitness;

	/**
	 * Constructs an instance of RunManager to be controlled by parameters
	 * retrieved from the given <code>Model</code>. Fitness evaluation will
	 * also be diverted to the given model.
	 * 
	 * @param model the model which will control the run with the parameters
	 *        and fitness function to use.
	 */
	public RunManager(final Model model) {
		this.model = model;

		// Setup core components.
		generation = new GenerationManager(model);
		initialisation = new InitialisationManager(model);

		// Configure parameters from the model.
		Life.get().addConfigListener(this, false);
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		noGenerations = model.getNoGenerations();
		terminationFitness = model.getTerminationFitness();
	}

	/**
	 * Executes a single evolutionary run of this <code>RunManager's</code>
	 * <code>Model</code>.
	 * 
	 * <p>
	 * If the number of generations obtained from the model is zero, then
	 * initialisation will be performed but no further generations. If the
	 * number of generations specified is one, then initialisation and one other
	 * generation will be performed.
	 * 
	 * <p>
	 * If the model is not in a runnable state after the the final call to
	 * configure fired at the start of this method execution then an illegal
	 * state exception will be thrown.
	 * 
	 * @param runNo the sequential number which identifies this run out of the
	 *        set of runs being performed.
	 */
	public void run(final int runNo) {
		// Give final opportunity to configure before starting the run.
		Life.get().fireConfigureEvent();

		// Validate that the model is in a runnable state.
		if (noGenerations < -1) {
			throw new IllegalStateException("number of generations must be -1 or greater: " + noGenerations);
		}

		// Inform everyone we're starting a run.
		Life.get().fireRunStartEvent();

		// Setup the run manager for a new run.
		bestProgram = null;
		bestFitness = Double.POSITIVE_INFINITY;
		final long startTime = System.nanoTime();

		// Add the run number to the available stats data.
		Stats.get().addData(RUN_NUMBER, runNo);

		// Perform initialisation.
		List<CandidateProgram> pop = initialisation.initialise();

		// Record best program so far and its fitness.
		updateBestProgram(pop);

		// Execute each generation.
		int gen = 1;
		while ((gen <= noGenerations) || (noGenerations == -1)) {
			// Perform the generation.
			pop = generation.generation(gen, pop);

			// Keep track of the best program and fitness.
			updateBestProgram(pop);

			// We might be finished?
			if (bestFitness <= terminationFitness) {
				Life.get().fireSuccessEvent();
				break;
			}

			gen++;
		}

		// Calculate how long the run took.
		final long runtime = System.nanoTime() - startTime;

		// Add run time to stats data.
		Stats.get().addData(RUN_TIME, runtime);

		// Inform everyone the run has ended.
		Life.get().fireRunEndEvent();
	}

	/*
	 * Update new best program.
	 * Note this forces us to evaluate all programs, which we might
	 * not have done if using TournamentSelection.
	 */
	private void updateBestProgram(final List<CandidateProgram> pop) {
		for (final CandidateProgram program: pop) {
			final double fitness = program.getFitness();
			if (fitness < bestFitness) {
				bestFitness = fitness;
				bestProgram = program;

				// Update the stats.
				Stats.get().addData(RUN_FITNESS_MIN, bestFitness);
				Stats.get().addData(RUN_FITTEST_PROGRAM, bestProgram);
			}
		}

		assert (bestProgram != null);
	}

	/**
	 * Retrieves this run manager's generation manager that will perform the
	 * execution of single evolutionary generations.
	 * 
	 * @return the generation manager that will handle execution of each
	 *         evolutionary generation.
	 */
	public GenerationManager getGenerationManager() {
		return generation;
	}

	/**
	 * Retrieves this run manager's initialisation manager that will perform the
	 * execution of the 0th evolutionary generations, that is generation of an
	 * initial population.
	 * 
	 * @return the initialisation manager that will handle execution of the 0th
	 *         evolutionary generation.
	 */
	public InitialisationManager getInitialisationManager() {
		return initialisation;
	}
}
