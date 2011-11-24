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

import java.util.*;

import org.epochx.life.*;
import org.epochx.representation.CandidateProgram;
import org.epochx.stats.Stats;

/**
 * This component handles the elitism operation to ensure the survival of the
 * most fit programs in a generation of an evolutionary run. This class
 * performs this task of scooping off and returning the best programs from a
 * population. The number of programs taken is decided by a call to the
 * model's <code>getNoElites</code> method.
 * 
 * <p>
 * Use of the elitism operation will generate the following events:
 * 
 * <table border="1">
 * <tr>
 * <th>Event</th>
 * <th>Revert</th>
 * <th>Modify</th>
 * <th>Raised when?</th>
 * </tr>
 * <tr>
 * <td>onElitismStart</td>
 * <td>no</td>
 * <td>no</td>
 * <td>Before the elitism operation is carried out.</td>
 * </tr>
 * <tr>
 * <td>onElitism</td>
 * <td>no</td>
 * <td><strong>yes</strong></td>
 * <td>After the elitism operation is carried out.</td>
 * </tr>
 * <tr>
 * <td>onElitismEnd</td>
 * <td>no</td>
 * <td>no</td>
 * <td>After the elitism operation has been completed.</td>
 * </tr>
 * </table>
 */
public class ElitismManager implements ConfigListener {

	// The controlling model.
	private final Model model;

	// The number of elites to be used.
	private int noElites;

	/**
	 * Constructs an instance of <code>Elitism</code> which will perform the
	 * evolutionary operation of elitism.
	 * 
	 * @param model the Model which defines the run parameters such as number
	 *        of elites to use.
	 */
	public ElitismManager(final Model model) {
		this.model = model;

		// Configure parameters from the model.
		Life.get().addConfigListener(this, false);
	}

	/*
	 * Configure component with parameters from the model.
	 */
	@Override
	public void onConfigure() {
		// Discover how many elites we need.
		noElites = model.getNoElites();
		final int popSize = model.getPopulationSize();
		noElites = (noElites < popSize) ? noElites : popSize;

		assert (noElites <= popSize);
	}

	/**
	 * Gets the best <code>CandidatePrograms</code> from the given population
	 * and returns them. The number of programs returned will be determined by
	 * a call to the model's <code>getNoElites()</code> method. If this method
	 * returns a value greater than the allowable population size then the
	 * population size will be used. Elites in EpochX are defined as the very
	 * best programs in a population.
	 * 
	 * <p>
	 * After selection and before returning, the model's life cycle listener
	 * will be informed of the elitism operation with a call to
	 * <code>onElitism()</code>. Unlike many of the other life cycle methods, it
	 * is not possible to 'revert' an elitism event by returning null. This is
	 * because elitism is a deterministic operation, and so re-running would
	 * lead to the same result.
	 * 
	 * @param pop the population from which elites are to be retrieved.
	 * @return a list containing the best CandidatePrograms determined by
	 *         fitness. If the models required number of elites is equal to or
	 *         greater than the population size then the returned list will
	 *         contain all CandidatePrograms from the population sorted.
	 */
	public List<CandidateProgram> elitism(final List<CandidateProgram> pop) {
		if (pop == null) {
			throw new IllegalArgumentException("pop size must not be null");
		}
		if (noElites < 0) {
			throw new IllegalStateException("no elites is less than 0");
		}

		Life.get().fireElitismStartEvent();

		// Record the start time.
		final long startTime = System.nanoTime();

		// Construct an array for elites.
		List<CandidateProgram> elites;

		if (noElites > 0) {
			// Sort the population and scoop off the best noElites.
			Collections.sort(pop);
			elites = new ArrayList<CandidateProgram>(pop.subList(pop.size() - noElites, pop.size()));
		} else {
			elites = new ArrayList<CandidateProgram>();
		}

		assert (elites.size() == noElites);

		final long runtime = System.nanoTime() - startTime;

		// Store the stats from the reproduction.
		Stats.get().addData(ELITE_PROGRAMS, elites);
		Stats.get().addData(ELITE_TIME, runtime);

		// Allow life cycle listener to confirm or modify.
		elites = Life.get().runElitismHooks(elites);

		Life.get().fireElitismEndEvent();

		return elites;
	}

}
