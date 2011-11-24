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

import org.epochx.life.Life;
import org.epochx.op.*;
import org.epochx.op.selection.TournamentSelector;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.Stats;
import org.epochx.tools.random.*;

/**
 * Implementations of <code>Model</code> define a configuration for a set of
 * evolutionary runs and provide the means to execute the runs. Instances of
 * model need to provide the components, control parameters and other settings
 * that the framework needs to evolve. The Model also provides the fitness
 * function against which all programs will be evaluated.
 * 
 * <p>
 * Prior to calling the <code>run</code> method it is typical to arrange for
 * some form of output to be generated each generation, each run or even each
 * crossover or mutation. A wide range of statistics are available from the
 * {@link Stats}. A listener model is employed through the {@link Life} to allow
 * events such as a generation starting, or crossover being carried out, to be
 * handled and responded to. This is commonly combined with the
 * <code>Stats</code> to output statistics each generation or run. Instances of
 * both these class are obtainable from a model.
 * 
 * <p>
 * The Model class implements <code>Runnable</code>. Because of this, it is
 * possible to run a model in its own thread simply by passing it to a thread
 * object which is then started.
 * 
 * <blockquote> <code>new Thread(model).start();</code> </blockquote>
 * 
 * <p>
 * Despite this, the current version of the <code>Model</code> class is not
 * threadsafe, so it is not advisable to start the same model multiple times
 * concurrently.
 * 
 * @see Stats
 * @see Life
 */
public abstract class Model {

	// Components.
	private RunManager run;

	// Operators.
	private PoolSelector poolSelector;
	private ProgramSelector programSelector;

	private Initialiser initialiser;
	private Crossover crossover;
	private Mutation mutation;

	// Control parameters.
	private RandomNumberGenerator randomNumberGenerator;

	private int noRuns;
	private int noGenerations;
	private int populationSize;
	private int poolSize;
	private int noElites;

	private double terminationFitness;
	private double crossoverProbability;
	private double mutationProbability;
	private double reproductionProbability;

	// Caching.
	private boolean cacheFitness;

	/**
	 * Construct the model with defaults.
	 */
	public Model() {
		// Control parameters.
		noRuns = 1;
		noGenerations = 50;
		populationSize = 100;
		poolSize = 50;
		noElites = 10;
		terminationFitness = 0.0;
		crossoverProbability = 0.9;
		mutationProbability = 0.1;
		reproductionProbability = 0.0;

		// Operators.
		programSelector = new TournamentSelector(this, 7);
		poolSelector = null;
		randomNumberGenerator = new MersenneTwisterFast();

		// Caching.
		cacheFitness = true;
	}

	/**
	 * Executes this model to perform a series of evolutionary runs.
	 * 
	 * <p>
	 * Calling this method directly will run this model sequentially, or it can
	 * be passed into a new thread object whose start() method is then called to
	 * run this model in a new thread.
	 * 
	 * A checks is performed after running the configure event that the model is
	 * in a runnable state. A model is in a runnable state if all compulsory
	 * control parameters and operators have been set. If it is not in a
	 * runnable state then an <code>IllegalStateException</code> is thrown.
	 */
	public void run() {
		run = new RunManager(this);

		// Fire config event.
		Life.get().fireConfigureEvent();

		// Validate that the model is in a runnable state.
		if (initialiser == null) {
			throw new IllegalStateException("no initialiser set");
		} else if ((crossover == null) && (crossoverProbability != 0.0)) {
			throw new IllegalStateException("no crossover set");
		} else if ((mutation == null) && (mutationProbability != 0.0)) {
			throw new IllegalStateException("no mutation set");
		}

		// Execute all the runs.
		for (int i = 0; i < getNoRuns(); i++) {
			run.run(i);
		}
	}

	/**
	 * Retrieves this model's run manager that will perform the task of
	 * executing single evolutionary runs according to this model.
	 * 
	 * @return the run manager that will handle execution of evolutionary runs.
	 */
	public RunManager getRunManager() {
		return run;
	}

	/**
	 * Calculates a fitness score of a program. In EpochX fitness is
	 * standardised so a fitness score of 0.1 is better than 0.2. It is
	 * essential that this method is implemented to provide a measure of how
	 * good the given program solution is.
	 * 
	 * @param program the candidate program to be evaluated.
	 * @return a fitness score for the program given as a parameter.
	 */
	public abstract double getFitness(CandidateProgram program);

	/**
	 * Retrieves the operator that is currently set to perform the operation of
	 * initialisation.
	 * 
	 * @return the operator that will perform initialisation.
	 */
	public Initialiser getInitialiser() {
		return initialiser;
	}

	/**
	 * Specifies the operator that should be used to perform the operation of
	 * initialisation.
	 * 
	 * @param initialiser the <code>Initialiser</code> to be responsible for
	 *        generating the initial population of the runs
	 */
	public void setInitialiser(final Initialiser initialiser) {
		if (initialiser != null) {
			this.initialiser = initialiser;
		} else {
			throw new IllegalArgumentException("initialiser must not be null");
		}

		assert (this.initialiser != null);
	}

	/**
	 * Retrieves the operator that is currently set to perform the genetic
	 * operation of crossover.
	 * 
	 * @return the operator that will perform crossover.
	 */
	public Crossover getCrossover() {
		return crossover;
	}

	/**
	 * Specifies the operator to perform the crossover operation.
	 * 
	 * @param crossover the <code>Crossover</code> to perform the exchange of
	 *        genetic material between two programs
	 */
	public void setCrossover(final Crossover crossover) {
		this.crossover = crossover;
	}

	/**
	 * Retrieves the operator that is currently set to perform the genetic
	 * operation of mutation.
	 * 
	 * @return the operator that will perform mutation.
	 */
	public Mutation getMutation() {
		return mutation;
	}

	/**
	 * Specifies the operator to perform the mutation operation.
	 * 
	 * @param mutation the <code>Mutation</code> that will carry out the
	 *        mutation of a program
	 */
	public void setMutation(final Mutation mutation) {
		this.mutation = mutation;
	}

	/**
	 * Returns whether fitness caching is to be used or not. Fitness caching
	 * should be used for most problems but if the same source code can be
	 * designated different fitness scores (due to the fitness being dependent
	 * upon other properties) then fitness caching should be disabled.
	 * 
	 * <p>
	 * Defaults to <code>true</code>.
	 * 
	 * @return true if fitness caching should be used, false otherwise.
	 */
	public boolean cacheFitness() {
		return cacheFitness;
	}

	/**
	 * Overwrites the default setting of whether to cache the fitness values.
	 * 
	 * @param cacheFitness whether fitnesses should be cached or not.
	 */
	public void setCacheFitness(final boolean cacheFitness) {
		this.cacheFitness = cacheFitness;
	}

	/**
	 * Returns the number of separate runs that will be carried out with this
	 * model.
	 * 
	 * <p>
	 * Defaults to <code>1</code>.
	 * 
	 * @return the number of runs that will be performed.
	 */
	public int getNoRuns() {
		return noRuns;
	}

	/**
	 * Overwrites the default number of runs.
	 * 
	 * @param noRuns the new number of runs to execute with this model.
	 */
	public void setNoRuns(final int noRuns) {
		if (noRuns >= 0) {
			this.noRuns = noRuns;
		} else {
			throw new IllegalArgumentException("noRuns must be zero or more");
		}

		assert (this.noRuns >= 0);
	}

	/**
	 * Returns the maximum number of generations that each run will contain.
	 * Termination may be caused by other criteria such as fitness.
	 * 
	 * <p>
	 * Defaults to 50.
	 * 
	 * @return the maximum number of generations that will be performed in a
	 *         run before termination.
	 */
	public int getNoGenerations() {
		return noGenerations;
	}

	/**
	 * Overwrites the default number of generations.
	 * 
	 * @param noGenerations the new number of generations to use within a run.
	 */
	public void setNoGenerations(final int noGenerations) {
		if (noGenerations >= -1) {
			this.noGenerations = noGenerations;
		} else {
			throw new IllegalArgumentException("noGenerations must be either -1 or greater: " + noGenerations);
		}

		assert (this.noGenerations >= -1);
	}

	/**
	 * Specifies the number of programs to maintain in the population.
	 * 
	 * <p>
	 * Defaults to 100.
	 * 
	 * @return the size of the population to be used.
	 */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * Overwrites the default population size of CandidatePrograms.
	 * 
	 * @param populationSize the new number of CandidatePrograms each generation
	 *        should contain.
	 */
	public void setPopulationSize(final int populationSize) {
		if (populationSize >= 1) {
			this.populationSize = populationSize;
		} else {
			throw new IllegalArgumentException("populationSize must be one or more");
		}

		assert (this.populationSize >= 1);
	}

	/**
	 * Returns the size of the breeding pool to use. If a value of zero or
	 * less is returned then the whole population will be used as the pool.
	 * 
	 * <p>
	 * Defaults to 50.
	 * 
	 * @return the number of programs that should form the breeding pool.
	 */
	public int getPoolSize() {
		return poolSize;
	}

	/**
	 * Overwrites the default pool size value.
	 * 
	 * @param poolSize the new size of the mating pool to use.
	 */
	public void setPoolSize(final int poolSize) {
		if ((poolSize == -1) || (poolSize >= 1)) {
			this.poolSize = poolSize;
		} else {
			throw new IllegalArgumentException("poolSize must be one or more");
		}

		assert (this.poolSize >= 1);
	}

	/**
	 * Returns the number of elite programs to be copied directly into each
	 * generation, where elites are the very best programs in a population.
	 * 
	 * <p>
	 * Defaults to 10.
	 * 
	 * @return the number of elites to be used each generation.
	 */
	public int getNoElites() {
		return noElites;
	}

	/**
	 * Overwrites the default number of elites to copy from one generation to
	 * the next.
	 * 
	 * @param noElites the new number of elites to copy across from one
	 *        population to the next.
	 */
	public void setNoElites(final int noElites) {
		if (noElites >= 0) {
			this.noElites = noElites;
		} else {
			throw new IllegalArgumentException("noElites must be zero or more");
		}

		assert (this.noElites >= 1);
	}

	/**
	 * Returns the probability that when choosing how the next programs will
	 * be generated, whether crossover will be used. The probabilities of
	 * crossover, mutation and reproduction should add up to 1.0. If the
	 * probabilities do not add up then crossover will be given the priority,
	 * followed by mutation, with the remaining probability left to
	 * reproduction.
	 * 
	 * <p>
	 * Defaults to 0.9 to represent a 90% chance.
	 * 
	 * @return the probability that the crossover operation will be carried
	 *         out.
	 */
	public double getCrossoverProbability() {
		return crossoverProbability;
	}

	/**
	 * Overwrites the default Crossover probability.
	 * 
	 * @param crossoverProbability the new Crossover probability to use.
	 */
	public void setCrossoverProbability(final double crossoverProbability) {
		if ((crossoverProbability >= 0.0) && (crossoverProbability <= 1.0)) {
			this.crossoverProbability = crossoverProbability;
		} else {
			throw new IllegalArgumentException("crossoverProbability must be between 0.0 and 1.0 inclusive");
		}

		assert ((this.crossoverProbability >= 0.0) && (this.crossoverProbability <= 1.0));
	}

	/**
	 * Returns the probability that when choosing how the next programs will
	 * be generated, whether mutation will be used. The probabilities of
	 * crossover, mutation and reproduction should add up to 1.0. If the
	 * probabilities do not add up then crossover will be given the priority,
	 * followed by mutation, with the remaining probability left to
	 * reproduction.
	 * 
	 * <p>
	 * Defaults to 0.1 to represent a 10% chance.
	 * 
	 * @return the probability that the mutation operation will be carried
	 *         out.
	 */
	public double getMutationProbability() {
		return mutationProbability;
	}

	/**
	 * Overwrites the default mutation probability.
	 * 
	 * @param mutationProbability the new mutation probability to use.
	 */
	public void setMutationProbability(final double mutationProbability) {
		if ((mutationProbability >= 0.0) && (mutationProbability <= 1.0)) {
			this.mutationProbability = mutationProbability;
		} else {
			throw new IllegalArgumentException("mutationProbability must be between 0.0 and 1.0 inclusive");
		}

		assert ((this.mutationProbability >= 0.0) && (this.mutationProbability <= 1.0));
	}

	/**
	 * Returns the probability that when choosing how the next programs will
	 * be generated, whether reproduction will be used. The probabilities of
	 * crossover, mutation and reproduction should add up to 1.0. If the
	 * probabilities do not add up then crossover will be given the priority,
	 * followed by mutation, with the remaining probability left to
	 * reproduction.
	 * 
	 * @return the probability that the reproduction operation will be carried
	 *         out.
	 */
	public double getReproductionProbability() {
		return reproductionProbability;
	}

	/**
	 * Overwrites the default reproduction probability.
	 * 
	 * @param reproductionProbability the new reproduction probability to use.
	 */
	public void setReproductionProbability(final double reproductionProbability) {
		if ((reproductionProbability >= 0.0) && (reproductionProbability <= 1.0)) {
			this.reproductionProbability = reproductionProbability;
		} else {
			throw new IllegalArgumentException("reproductionProbability must be between 0.0 and 1.0 inclusive");
		}

		assert ((this.reproductionProbability >= 0.0) && (this.reproductionProbability <= 1.0));
	}

	/**
	 * Returns the target fitness score. The current run will be terminated if a
	 * fitness score equal to or less than this value is achieved. If this
	 * happens the run will be considered a success. If the implementer doesn't
	 * wish to use a fitness termination criterion then a fitness score lower
	 * than the lowest possible value should be used, such as <code>
	 * Double.NEGATIVE_INFINITY</code>.
	 * 
	 * <p>
	 * Defaults to 0.0.
	 * 
	 * @return the fitness score that will be used as the fitness termination
	 *         criterion.
	 */
	public double getTerminationFitness() {
		return terminationFitness;
	}

	/**
	 * Overwrites the default fitness for run termination.
	 * 
	 * @param terminationFitness the new fitness below which a run will be
	 *        terminated.
	 */
	public void setTerminationFitness(final double terminationFitness) {
		this.terminationFitness = terminationFitness;
	}

	/**
	 * Returns the component to be used to select individual programs from a
	 * breeding pool to undergo operations such as crossover and mutation.
	 * 
	 * <p>
	 * Defaults to an instance of {@link TournamentSelector} with a tournament
	 * size of 7.
	 * 
	 * @return the <code>ProgramSelector</code> that will be used to select the
	 *         programs to undergo operations.
	 */
	public ProgramSelector getProgramSelector() {
		return programSelector;
	}

	/**
	 * Overwrites the default parent selector used to select parents to undergo
	 * a genetic operator from either a pool or the previous population.
	 * 
	 * @param programSelector the new ProgramSelector to be used when selecting
	 *        parents for a genetic operator.
	 */
	public void setProgramSelector(final ProgramSelector programSelector) {
		if (programSelector != null) {
			this.programSelector = programSelector;
		} else {
			throw new IllegalArgumentException("program selector must not be null");
		}

		assert (this.programSelector != null);
	}

	/**
	 * Returns the component to perform the selection of a breeding pool. A
	 * <code>null</code> pool selector will result in no pool selection being
	 * performed, and programs will be selected directly from the population.
	 * 
	 * <p>
	 * Defaults to <code>null</code>.
	 * 
	 * @return the <code>PoolSelector</code> that will select the breeding pool
	 *         of programs.
	 */
	public PoolSelector getPoolSelector() {
		return poolSelector;
	}

	/**
	 * Overwrites the default pool selector used to generate a mating pool.
	 * 
	 * @param poolSelector the new PoolSelector to be used when building a
	 *        breeding pool
	 */
	public void setPoolSelector(final PoolSelector poolSelector) {
		this.poolSelector = poolSelector;
	}

	/**
	 * Returns the random number generator that to be responsible for
	 * determining random behaviour. It is important that the random number
	 * generator is not unnecessary re-constructed on each call to this method
	 * as that could reduce the quality of the random numbers.
	 * 
	 * <p>
	 * Evolutionary algorithms are inherently non-deterministic, so the result
	 * of multiple calls to the <code>run</code> method with identical models
	 * will naturally produce different results if the random number generator
	 * in use by the model is seeded differently.
	 * 
	 * <p>
	 * Defaults to {@link MersenneTwisterFast}.
	 * 
	 * @return the random number generator to be provide the random numbers.
	 */
	public RandomNumberGenerator getRNG() {
		return randomNumberGenerator;
	}

	/**
	 * Overwrites the default random number generator used to generate random
	 * numbers to control behaviour throughout a run.
	 * 
	 * @param rng the random number generator to be used any time random
	 *        behaviour is required.
	 */
	public void setRNG(final RandomNumberGenerator rng) {
		if (rng != null) {
			randomNumberGenerator = rng;
		} else {
			throw new IllegalArgumentException("random number generator must not be null");
		}

		assert (randomNumberGenerator != null);
	}
}
